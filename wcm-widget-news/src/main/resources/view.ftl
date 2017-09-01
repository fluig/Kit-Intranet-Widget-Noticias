<#attempt>
<#if !orderByField??>
    <#assign orderByField = "documentid">
</#if>
<#assign parameters = '{"numberOfArticles": "${numberOfArticles!5}", "widgetVersion" : "${applicationVersion}", "orderByField": "${orderByField}"}'?html>

<div id="KitIntranetNews_${instanceId}"	class="wcm-widget-class super-widget fluig-style-guide"	data-params="KitIntranetNews.instance(${parameters})">
	
	<h2 class="page-header">
		<span class="fluigicon fluigicon-newspaper fluigicon-md"></span>
		 ${i18n.getTranslation('application.title')}
	</h2>
	<div class="row">
		<div class="col-xs-12">
    		<ul class="list-group fs-no-margin-bottom" data-list-news></ul>
		</div>
	</div>
	<div class="row" data-row-load-more>
		<div class="col-xs-12 text-center">
			<button type="button" class="btn btn-default btn-md btn-block" data-load-more-news>${i18n.getTranslation('kit_news.load.more')}</button>
		</div>
	</div>
    	
	<script type="text/template" class="template-news">
		<li class="list-group-item fs-no-border-left fs-no-border fs-cursor-pointer fs-no-padding-left fs-no-padding-right" data-show-news-detail data-array-position="{{arrayPosition}}">
			<div class="media">
			    <a class="pull-left" href="#">
			    	<img width="120" class="media-object img-rounded" src="{{imgUrl}}" alt="">
			    </a>
		        <h2 class="media-heading">{{news_title}}<br></h2>
		        <h4><small>{{#formatPublishDate}}{{formatPublishDate}}{{/formatPublishDate}}</small></h4>
				<p class="hidden-xs">{{news_body_min}}</p>
				<button type="button" class="btn btn-default hidden-xs">${i18n.getTranslation('kit_news.read.full')}</button>
			</div>
			<br>
		</li>
	</script>
	
	<script type="text/template" class="template-no-news">
		<div class="alert alert-info">
			${i18n.getTranslation('kit_news.no.news')}
		</div>
	</script>
	
	
	<script type="text/template" class="template-news-detail">
		<div class="container-fluid">
			<h2 class="page-header">{{news_title}}</h2>
			{{#imgUrl}}
				<div class="row">
					<div class="col-xs-12 text-center">
						<img style="max-width: 100%; max-height: 300px;" class="img-rounded" src="{{imgUrl}}" alt="">
						<br><br>
					</div>
				</div>
			{{/imgUrl}}
			<div class="row">
				<div class="col-xs-12">
					<p class="kit-news-body">{{{news_body}}}</p>
				</div>
			</div>
		</div>
	</script>
</div>

<script src="/webdesk/vcXMLRPC.js" type="text/javascript"></script>
<#recover>
	<#include "/social_error.ftl">
</#attempt>