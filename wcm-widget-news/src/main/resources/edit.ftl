<#if !newsSource??>
    <#assign newsSource = "#">
</#if>

<#if !numberOfArticles??>
    <#assign numberOfArticles = 5>
</#if>
<#if !orderByField??>
    <#assign orderByField = "documentid;desc">
</#if>

<#assign parameters = '{"newsSource": "${newsSource}", "numberOfArticles": "${numberOfArticles!5}", "orderByField": "${orderByField}"}'?html>

<div id="KitIntranetNews_${instanceId}" class="wcm-widget-class super-widget fluig-style-guide"	data-params="KitIntranetNews.instance(${parameters})">

    <div class="container-fluid">
		<div class="row">
			<div class="col-xs-12">
				<form role="form">
				    <div class="form-group">
				        <label for="formLink">${i18n.getTranslation('kit_news.form.link')}</label><br>
				        <a data-form-link href="${newsSource!}" class="fs-word-break"></a>
				    </div>
				    <div class="form-group">
				        <label for="numberOfRecords">${i18n.getTranslation('kit_news.register.limit')}</label>
				        <input type="number" class="form-control" id="numberOfArticles" placeholder="" value="${numberOfArticles}" data-number-of-articles>
				        <p class="help-block"><small>${i18n.getTranslation('kit_news.register.limit.helper')}</small></p>
				    </div>
				    <div class="form-group">
				    	<label for="orderByField">${i18n.getTranslation('kit_news.order.by')}</label>
					    <select class="form-control" id="orderByField">
							<option value="documentid;desc">${i18n.getTranslation('kit_news.field.id')}</option>
							<option value="news_title;asc">${i18n.getTranslation('kit_news.field.title.asc')}</option>
							<option value="news_title;desc">${i18n.getTranslation('kit_news.field.title.desc')}</option>
							<option value="publishDate;asc">${i18n.getTranslation('kit_news.field.publish.date.asc')}</option>
							<option value="publishDate;desc">${i18n.getTranslation('kit_news.field.publish.date.desc')}</option>
						</select>
					</div>
					
						<div class="radio">
   							 <label>
    							<input type="radio" data-all_news name="optionsRadios" id="optionsRadios1" value="option1" checked>
    							${i18n.getTranslation('kit_news.field.publish.all_news')}
   							 </label>
						</div>
						<div class="radio">
    						<label>
    							<input type="radio" data-recent_news name="optionsRadios" id="optionsRadios2" value="option2">
   								 ${i18n.getTranslation('kit_news.field.publish.recent_news')}
    					</label>
						</div>

					
				    <div class="form-group">
				    	<div class="text-right">
				   			<button type="button" class="btn btn-primary" data-save-settings>${i18n.getTranslation('kit_news.save.settings')}</button>
					   	</div>
					</div>
				</form>
			</div>
		</div>
	</div>

</div>
