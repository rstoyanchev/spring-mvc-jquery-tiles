<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div>
	<tiles:insertAttribute name="search"/>
</div>
<div>
	<div class="span-16 colborder">
		<tiles:insertAttribute name="list"/>
	</div>
	<div class="span-7 last">
		<tiles:insertAttribute name="detail"/>
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		var ajaxify = function() {
			$(".ajaxForm").submit(function() {
				var data = $(this).serialize() + "&htmlFormat=nolayout";
				$.ajax({
					type : $(this).attr("method"),
					url : $(this).attr("action"), 
					data : data, 
					success : function(data) {
						var id = $(data).attr("id");
						$("#" + id).replaceWith(data);
						ajaxify();
					}
				});
				return false;  
			});
			$(".ajaxLink").click(function() {
				$.ajax({
					type : 'GET',
					url : $(this).attr("href"),
					data : "htmlFormat=nolayout",
					success : function(data) {
						var id = $(data).attr("id");
						$("#" + id).replaceWith(data);
						ajaxify();
					}
				});
				return false;
			});
		}
		ajaxify();
	});
</script>
