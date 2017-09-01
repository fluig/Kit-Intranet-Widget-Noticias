<div class="row clearfix">
    <div class="col-md-12">
        <h2>
        ${title!}
        </h2>
        <hr class="fs-transparent-25 fs-no-margin-top fs-no-margin-bottom"/>
    </div>

    <div class="media col-md-12">
        <div class="pull-left fs-no-padding-left col-md-5">
            <img class="media-object fs-no-padding-left fs-xs-space col-md-12 newsImage" src="${imgURL!}"/>
        </div>
	    <span class="media-body">
        ${content?replace("\\r\\n","<br/>")!}
        </span>
    </div>
</div>