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
  	function setLstParam()
  	{
  	  	//list all paramettre of the form before sending to CreateForm.java
		var param="",strName="";
		var x = document.getElementById("newForm");
		for (var i=0; i<x.length; i++)
		{
			strName=x.elements[i].getAttribute("name");
			if(strName!=null)
				param += strName+"<msf_split>";
		}
		var ch;
	 	//var prefixCB="Patient-";
	 	//var subfixCB="-comboValue.txt";
	 	//var prefixCBDis="Encounter-";
	 	//var subfixCBDis="-comboValue.txt";
		for (var i=0; i<document.newForm.msf_checked.length; i++)
		{
			ch=document.newForm.msf_checked[i];
			if(!ch.checked){
				param=param.replace("<msf_labelEn>"+ch.value+"<msf_split>", "");
				param=param.replace("<msf_typeEn>"+ch.value+"<msf_split>", "");
				param=param.replace("<msf_labelEnDis1>"+ch.value+"<msf_split>", "");
				param=param.replace("<msf_typeEnDis1>"+ch.value+"<msf_split>", "");
				param=param.replace("<msf_labelEnDis2>"+ch.value+"<msf_split>", "");
				param=param.replace("<msf_typeEnDis2>"+ch.value+"<msf_split>", "");
				param=param.replace("<msf_labelEnDis3>"+ch.value+"<msf_split>", "");
				param=param.replace("<msf_typeEnDis3>"+ch.value+"<msf_split>", "");
				
				//param=param.replace("<msf_comboFile>"+prefixCB+ch.value+subfixCB+"<msf_split>", "");
				//param=param.replace("<msf_comboFile>"+prefixCBDis+ch.value+subfixCBDis+"<msf_split>", "");
				
			}
		}

		//remove uncheck of combo value
		var cb_ch;
		for (var i=0; i<document.newForm.msf_combo_checked.length; i++)
		{
			cb_ch=document.newForm.msf_combo_checked[i];
			if(!cb_ch.checked)
			{
				param=param.replace(cb_ch.value+"<msf_split>", "");
			}
		}
		
		var form=document.getElementById("newForm");
		var paraml=document.createElement("input");
		paraml.setAttribute("type", "hidden");
		paraml.setAttribute("name", "lstFiledName");
		paraml.setAttribute("value",param);
		form.appendChild(paraml);
		
  	}

  	
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