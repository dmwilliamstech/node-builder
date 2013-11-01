<footer class="footer">
	<div class="container">
	  <div class="row">

          </div>

        <p class="pull-left">
            <div>  
              <sec:loggedInUserInfo field="organizations" />  
            </div>
            <div id="footer_text" class=${node.builder.Config.globalConfig.get('application.footer_text_color')}>
              ${node.builder.Config.globalConfig.get('application.footer_text')}
            </div>
        </p>

        <p class="pull-right">
            <a href="#">Back to top</a>
        </p>
	</div>
</footer>
