<style>
	.ontop {
		z-index: 999;
		width: 100%;
		height: 700%;
		top: 0;
		left: 0;
		display: none;
		position: absolute;				
		background-color: #cccccc;
		color: #aaaaaa;
		opacity: 1;
		filter: alpha(opacity = 50);
	}
	#popup {
		position: absolute;
		color: #000000;
		background-color: #ffffff;
		top: 150px;
		left: 40%;
		margin-top: -100px;
		margin-left: -200px;
		
	}
</style>

<script type="text/javascript">
  
  	
	function toggle(elementId) {
		var ele = document.getElementById(elementId);
		if(ele.style.display == "block") {
	    		ele.style.display = "none";
	    		
	  	}
		else {
			ele.style.display = "block";
		}
	} 
	function collapse(elementId) {
		var ele = document.getElementById(elementId);
	   	ele.style.display = "none";
	} 
	
	function pop(div) {
		document.getElementById(div).style.display = 'block';
	}
	
	function hide(div) {
		document.getElementById(div).style.display = 'none';
	}
	//To detect escape button
	document.onkeydown = function(evt) {
		evt = evt || window.event;
		if (evt.keyCode == 27) {
			hide('popDiv');
		}
	};	
</script>