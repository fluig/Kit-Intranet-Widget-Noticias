package com.fluig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.fluig.sdk.api.FluigAPI;
import com.fluig.sdk.api.common.SDKException;
import com.fluig.sdk.api.document.DocumentVO;
import com.fluig.sdk.api.workflow.AttachmentVO;
import com.fluig.sdk.api.workflow.CardIndexAttachmentVO;
import com.fluig.sdk.api.workflow.CardIndexVO;
import com.fluig.sdk.api.workflow.CardItemVO;

public class FormUtils {

    private static final String FORM_PATH_CHILDREN = "/form/";
    private static final String FORMDATA_PATH_CHILDREN = "/formdata/";
    private static final String ICON_PATH_CHILDREN = "/icon/";
    private static final int END_OF_FILE = -1;
    private static final String DEFAULT_ATTATCHMENT_ICON_NAME = "padrao.png";
    public static final String LOCALE_PT_BR = "pt_BR";
    public static final String LOCALE_EN_US = "en_US";
    public static final String LOCALE_ES = "es";
    private static final String VARIABLE_DAY = "{day}";
    private static final String VARIABLE_MONTH = "{month}";
    private static final String FORM_TITLE_PROP = "kit_news.title";

    private final String warName;

    private FluigAPI fluigAPI;

    /**
     * @param warName Just de file name. You must not specify its path.
     * @throws Exception
     */
    public FormUtils(String warName) throws Exception {
        this.warName = warName;
    }

    /**
     * Move items to upload directory
     * @return All itens under "form" directory
     * @throws Exception
     */
    public List<CardIndexAttachmentVO> uploadFormFilesAndGenerateVOs() throws Exception {
        List<CardIndexAttachmentVO> fileList = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(getZipFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if (entries != null) {
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (isUnderFormDirectory(entry)) {
                        String fileName = getName(entry.getName());
                        getFluigAPI().getContentFilesService().upload(fileName, getContent(zipFile, entry));
                        CardIndexAttachmentVO vo = new CardIndexAttachmentVO();
                        vo.setAttach(true);
                        vo.setFileName(fileName);
                        vo.setPrincipal(false);
                        fileList.add(vo);
                    }
                }
            }
        }
        return fileList;
    }

    public String getFormTitle(String localeSufix) throws Exception {
        String formTitle = null;
        List<Map<String, String>> records = getFormData(localeSufix);
        if (records != null && !records.isEmpty()) {
            for (Map<String, String> record : records) {
                if(record.containsKey(FORM_TITLE_PROP)){
                    formTitle = record.get(FORM_TITLE_PROP);
                }
            }
        }
        return formTitle;
    }

    private List<Map<String, String>> getFormData(String localeSufix) throws Exception {
        List<Map<String, String>> records = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(getZipFile())) {

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if (entries != null) {
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (isUnderFormDataDirectory(entry, localeSufix)) {
                        Properties prop = new Properties();
                        prop.load(zipFile.getInputStream(entry));
                        Set<String> keys = prop.stringPropertyNames();

                        Map<String, String> record = new HashMap<>();
                        for (String key : keys) {
                            String value = prop.getProperty(key);
                            record.put(key, value);
                        }
                        records.add(record);
                    }
                }
            }
        }
        return records;
    }

    /**
     * Move items to upload directory
     * @return All itens under "icon" directory
     * @throws Exception
     */
    public List<AttachmentVO> uploadIconFilesAndGenerateVOs(String... iconFileNames) throws Exception {
        Map<String, String> uploadMap = mapIcons(iconFileNames);
        List<AttachmentVO> fileList = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(getZipFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if (entries != null) {
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (isUnderIconDirectory(entry)) {
                        String fileName = getName(entry.getName());
                        if (uploadMap.containsKey(fileName.trim().toLowerCase())) {
                            upload(fileName, getContent(zipFile, entry));
                            fileList.add(mapAttachmentVO(new File(fileName)));
                        }
                    }
                }
            }
        }
        return fileList;
    }

    private AttachmentVO mapAttachmentVO(File file) {
        AttachmentVO vo = new AttachmentVO();
        vo.setAttach(true);
        vo.setFileName(DEFAULT_ATTATCHMENT_ICON_NAME);
        vo.setPrincipal(false);
        vo.setAbsoluteFileName(file.getName());
        return vo;
    }

    private void upload(String fileName, byte[] content) throws SDKException {
        getFluigAPI().getContentFilesService().upload(fileName, content);
    }

    private Map<String, String> mapIcons(String[] iconFileNames) {
        Map<String, String> map = new TreeMap<>();
        if (iconFileNames != null && iconFileNames.length > 0) {
            for (int i = 0; i < iconFileNames.length; i++) {
                String iconFileName = iconFileNames[i];
                if (iconFileName != null && !iconFileName.trim().isEmpty()) {
                    map.put(iconFileName.trim().toLowerCase(), iconFileName);
                }
            }
        }
        return map;
    }

    private File getZipFile() throws Exception {
        if (warName != null && !warName.trim().isEmpty()) {
            File warFile = new File(getFluigAPI().getContentFilesService().getDeployableArtifactsDirectory()
                    , warName);
            if (warFile.exists() && warFile.isFile()) {
                return warFile.getAbsoluteFile();
            } else {
                throw new Exception("Arquivo '" + warFile.getAbsolutePath()
                        + "' não foi encontrado no local esperado");
            }
        } else {
            throw new Exception("não foi informado um arquivo .war para ser extraído");
        }
    }

    private byte[] getContent(ZipFile zipFile, ZipEntry entry) throws IOException {
        InputStream stream = zipFile.getInputStream(entry);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int bytesRead;
        byte[] buffer = new byte[1024];
        while ((bytesRead = stream.read(buffer, 0, buffer.length)) != END_OF_FILE) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        return output.toByteArray();
    }

    private String getName(String name) {
        String[] entries = splitPathName(name);
        return entries[entries.length - 1];
    }

    private String[] splitPathName(String name) {
        String separator = getPathSeparator(name);
        String[] split = name.split(separator);
        return split;
    }

    private String getPathSeparator(String name) {
        if (name.contains("/")) {
            return "/";
        } else {
            return "\\";
        }
    }

    private boolean isUnderIconDirectory(ZipEntry entry) {
        return isUnderACertainDirectory(entry, ICON_PATH_CHILDREN);
    }

    private boolean isUnderFormDirectory(ZipEntry entry) {
        return isUnderACertainDirectory(entry, FORM_PATH_CHILDREN);
    }

    private boolean isUnderFormDataDirectory(ZipEntry entry, String localeSufix) {
        return isUnderACertainDirectory(entry, FORMDATA_PATH_CHILDREN + localeSufix);
    }


    private boolean isUnderACertainDirectory(ZipEntry entry, String directory) {
        String file = entry.getName().trim().toLowerCase();
        if (file.contains(directory.toLowerCase())) {
            char lastLetter = file.substring(file.length() - 1).toCharArray()[0];
            return lastLetter != '/' && lastLetter != '\\';
        } else {
            return false;
        }
    }

    public FluigAPI getFluigAPI() throws SDKException {
        if (fluigAPI == null) {
            fluigAPI = new FluigAPI();
        }
        return fluigAPI;
    }

    public Integer findDirectoryID(int directoryIDToLook, String directoryDescription) throws SDKException {
        List<DocumentVO> itens = getFluigAPI().getFolderDocumentService().list(directoryIDToLook);
        for (DocumentVO vo: itens) {
            String name = vo.getDocumentDescription();
            if (name != null && !name.trim().isEmpty()) {
                if (name.trim().toLowerCase().equals(directoryDescription.trim().toLowerCase())) {
                    return vo.getDocumentId();
                }
            }
        }
        return null;
    }

    private CardItemVO createCard(Map<String, String> record, CardIndexVO cardIndexVO) throws Exception {
        CardItemVO cardItem = new CardItemVO();
        cardItem.setDocumentDescription(record.get("recordDescription"));
        cardItem.setTenantId(cardIndexVO.getTenantId());
        cardItem.setVersion(1000);
        cardItem.setDocumentType("5");
        cardItem.setParentDocumentId(cardIndexVO.getDocumentId());
        cardItem.setDocumentTypeId("");
        cardItem.setUserNotify(false);

        ArrayList<Map<String, String>> formData = new ArrayList<>();

        Set<String> keys = record.keySet();

        for (String key : keys) {
            if(key.equals("icon")){
                continue;
            }
            Map<String, String> recordData = new HashMap<>();
            recordData.put("name", key);
            recordData.put("value", parseValue(record.get(key)));

            formData.add(recordData);
        }

        cardItem.setFormData(formData);

        cardItem.setColleagueId("adm");

        List<AttachmentVO> attachmentVOs = uploadIconFilesAndGenerateVOs(record.get("icon"));
        if(attachmentVOs != null){
            cardItem.setAttachments(attachmentVOs);
        }

        return cardItem;
    }

    private void saveRecord(CardItemVO cardItemVO) {
        try {
            getFluigAPI().getCardService().createItem(cardItemVO);
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    public void createRecords(CardIndexVO cardIndexVO, String localeSufix) throws Exception {
        List<Map<String, String>> records = getFormData(localeSufix);
        if (records != null && !records.isEmpty()) {
            for (Map<String, String> record : records) {
                if(record.containsKey(FORM_TITLE_PROP)){
                    continue;
                }
                CardItemVO card = createCard(record, cardIndexVO);
                saveRecord(card);
            }
        }
    }

    public boolean checkIfFormExists(Integer formsDirectory) throws Exception {
        List<String> formNames = getFormNames();
        boolean formExists = false;
        for (String formName : formNames) {
            Integer directoryID = findDirectoryID(formsDirectory, formName);
            if(directoryID != null){
                formExists = true;
                break;
            }
        }
        return formExists;
    }

    private List<String> getFormNames() throws Exception {
        List<String> formNames = new ArrayList<>();
        List<String> localeNames = new ArrayList<>(3);
        localeNames.add(LOCALE_EN_US);
        localeNames.add(LOCALE_ES);
        localeNames.add(LOCALE_PT_BR);
        for (String locale : localeNames) {
            List<Map<String, String>> records = getFormData(locale);
            for (Map<String, String> record : records) {
                if (record.containsKey(FORM_TITLE_PROP)) {
                    formNames.add(record.get(FORM_TITLE_PROP));
                    break;
                }
            }
        }

        return formNames;
    }

    private String parseValue(String value) {
        if (value != null && value.contains("{")) {
            String var = value.trim().toLowerCase();
            Integer dateMarker = getDateMarker(var);
            if (dateMarker!= null) {
                Calendar cal = Calendar.getInstance();
                String incValue = var.substring(var.lastIndexOf("}") + 1);
                if (incValue != null && !incValue.trim().isEmpty()) {
                    int valueToBeAdded;
                    try {
                        valueToBeAdded = Integer.valueOf(incValue.trim()).intValue();
                    } catch (Exception e) {
                        valueToBeAdded = 0;
                    }
                    cal.add(dateMarker.intValue(), valueToBeAdded);
                }
                int result = cal.get(dateMarker.intValue());
                switch (dateMarker) {
                    case Calendar.MONTH:
                        return String.valueOf(result + 1);
                    default:
                        return String.valueOf(result);
                }
            }
        }
        return value;
    }

    private Integer getDateMarker(String arg) {
        if (arg.contains(VARIABLE_DAY)) {
            return Calendar.DAY_OF_MONTH;
        } else if (arg.contains(VARIABLE_MONTH)) {
            return Calendar.MONTH;
        } else {
            return null;
        }
    }

}
