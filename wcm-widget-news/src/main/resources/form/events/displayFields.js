function displayFields(form, customHTML) {
    form.setValue("publishDate", new Date().getTime());

    var newsTitle = form.getValue('news_body');
    newsTitle = newsTitle.replaceAll("\"", "'");
    form.setValue("news_title", newsTitle);

    var newsContent = form.getValue('news_body');
    newsContent = newsContent.replaceAll("\"", "'");
    form.setValue("news_body", newsContent);

}
