function validateForm(form) {
    form.setValue('publishDate', '08/08/2015 14:14');

    if (form.getValue('news_title') == null || form.getValue('news_title') == '') {
        throw i18n.translate("kit_news.validation.title");
    }

    if (form.getValue('news_body') == null || form.getValue('news_body') == '') {
        throw i18n.translate("kit_news.validation.content");
    }
}
