<footer class="footer">
	<div class="container">
	  <div class="row">

          </div>

        <p class="pull-left">
            <div>  
              <sec:loggedInUserInfo field="organizations" />  
            </div>
            <div id="footerCopyrightText" class=${node.builder.Config.globalConfig.get('application.footer.text.color')}>
              ${node.builder.Config.globalConfig.get('application.footer.text')}
            </div>
        </p>

        <p class="pull-right">
            <a href="#">Back to top</a>
        </p>
	</div>
</footer>
