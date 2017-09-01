<div class="row clearfix">
    <div class="col-md-12">
        <h2 class="fs-cursor-pointer" document-id="${documentid!}" data-more-news>
        	${title!}
        </h2>
    </div>
	<span class="text-muted col-md-12 small">
	    ${publishDate!}
	</span>

	<div class="media col-md-12">
    	<div class="pull-left fs-no-padding-left col-md-5">
   			<img class="media-object fs-no-padding-left fs-xs-space col-md-12 newsImage" src="${imgURL!}"/>
    	</div>
	    <span class="media-body">
			${content?replace("\\r\\n","<br/>")!}
	    </span>
	</div>
</div>
<br/>
<button type="submit" class="btn btn-sm btn-default" document-id="${documentid!}" data-more-news>
	${i18n.getTranslation('kit_news.morenews')}
</button>
<hr class="fs-transparent-25 fs-no-margin-bottom"/>
