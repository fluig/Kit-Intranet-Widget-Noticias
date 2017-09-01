package com.fluig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.fluig.sdk.api.common.SDKException;
import com.fluig.sdk.api.document.DocumentApproverVO;
import com.fluig.sdk.api.document.FolderVO;
import com.fluig.sdk.api.workflow.CardIndexAttachmentVO;
import com.fluig.sdk.api.workflow.CardIndexVO;
import com.fluig.sdk.user.UserVO;

public class CardIndexCreator {
    private static final String HTML_EXTENSION = ".html";
    private static final String HTM_EXTENSION = ".htm";
    private static final String TENANT_ID_KEY = "tenantId";
    private static final String INSTANCE_ADMINISTRATOR = "wcmadmin";
    private static final String KIT_INTRANET_FORMS_DIRECTORY = "Kit Intranet";

    /**
     * @param warFilePathName it must have a directory called "form" inside.
     * @param locale - pt_BR, en_US or es
     * @throws SDKException
     */
    public CardIndexVO createCardIndex(String warFilePathName, String locale) throws Exception {

        FormUtils formUtils = new FormUtils(warFilePathName);

        UserVO user = formUtils.getFluigAPI().getUserService().getCurrent();
        validateUser(user);

        Integer formsDirectory = getKitFormsDirectory(formUtils, user);

        if (!formUtils.checkIfFormExists(formsDirectory)) {
            List<CardIndexAttachmentVO> fileVOs = formUtils.uploadFormFilesAndGenerateVOs();
            String datasetName = getDatasetName(fileVOs);
            CardIndexVO vo = new CardIndexVO();
            vo.setDatasetName(datasetName);
            vo.setDocumentDescription(formUtils.getFormTitle(locale));
            vo.setCardDescription("news_title");
            vo.setTenantId(getCurrentTenantID(user));
            vo.setPublisherId(user.getCode());
            vo.setParentDocumentId(formsDirectory);
            vo.setAttachments(fileVOs);
            vo.setPersistenceType(1);
            displayInformation(vo);

            try {
                CardIndexVO cardIndexVO = formUtils.getFluigAPI().getCardIndexService().create(vo);
                return cardIndexVO;
            } catch (Exception e) {
                System.out.println("Não foi possível criar o formulário '" + vo.getDocumentDescription() + "' pela seguinte razão: ");
                System.out.println(e.getMessage());
                return null;
            }
        } else {
            System.out.println("formulario já existe");
            return null;
        }
    }

    private Integer getKitFormsDirectory(FormUtils formUtils, UserVO user) throws SDKException {
        Integer formsDirectory = formUtils.getFluigAPI().getCardIndexService().getDefaultFormFolderId();
        Integer kitFormsFolderID = formUtils.findDirectoryID(formsDirectory, KIT_INTRANET_FORMS_DIRECTORY);
        if (kitFormsFolderID == null) {
            FolderVO folder = formUtils.getFluigAPI().getFolderDocumentService().create(createFormsDirectoryVO(formsDirectory, user));
            return folder.getDocumentId();
        } else {
            return kitFormsFolderID;
        }
    }

    private FolderVO createFormsDirectoryVO(Integer parentID, UserVO user) throws SDKException {
        FolderVO vo = new FolderVO();
        vo.setParentFolderId(parentID);
        vo.setPublisherId(user.getCode());
        vo.setTenantId(getCurrentTenantID(user));
        vo.setAdditionalComments("Created for Kit Intranet");
        vo.setApprovalAndOr(false);
        vo.setColleagueId(user.getCode());
        vo.setCreateDate(Calendar.getInstance().getTime());
        vo.setDocumentDescription(KIT_INTRANET_FORMS_DIRECTORY);
        vo.setDownloadEnabled(false);
        vo.setExpires(false);
        vo.setIconId(null);
        vo.setImutable(false);
        vo.setInheritSecurity(true);
        vo.setInternalVisualizer(true);
        vo.setKeyWord("");
        vo.setNotifyUser(false);
        vo.setVersion(1);
        vo.setPublisherApprovers(new ArrayList<DocumentApproverVO>());
        return vo;
    }

    private void validateUser(UserVO user) throws Exception {
        if (user.getLogin().trim().toLowerCase().equals(INSTANCE_ADMINISTRATOR)) {
            throw new Exception("It is not possible to execute this action with the Fluig Instance Adminstrator. "
                    + "Try again with a Tenant Administrator.");
        }
    }

    private void displayInformation(CardIndexVO vo) {
        System.out.println("Creating form '" + vo.getCardDescription() + "'...");
        for (CardIndexAttachmentVO file : vo.getAttachments()) {
            System.out.print("Creating attatchment '" + file.getFileName() + "'...");
        }
    }

    private Long getCurrentTenantID(UserVO user) throws SDKException {
        Object data = user.getExtraData(TENANT_ID_KEY);
        Long tenantId = Long.valueOf(String.valueOf(data));
        return tenantId;
    }

    private String getDatasetName(List<CardIndexAttachmentVO> fileVOs) throws Exception {
        if (fileVOs != null && !fileVOs.isEmpty()) {
            for (int i = 0; i < fileVOs.size(); i++) {
                String fileName = fileVOs.get(i).getFileName().trim().toLowerCase();
                if (isHtml(fileName)) {
                    fileVOs.get(i).setPrincipal(true);
                    String datasetName = fileName.substring(0, fileName.lastIndexOf("."));
                    return datasetName;
                }
            }
        }
        throw new Exception("No html file was found. Check your form structure");
    }

    private boolean isHtml(String fileName) {
        return fileName.endsWith(HTML_EXTENSION) || fileName.endsWith(HTM_EXTENSION);
    }

}
