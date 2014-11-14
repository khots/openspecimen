<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
    <script language="JavaScript" type="text/javascript" src="dhtmlxSuite_v35/dhtmlxGrid/codebase/dhtmlxcommon.js"></script>
	<script  language="JavaScript" type="text/javascript"src="dhtmlxSuite_v35/dhtmlxGrid/codebase/dhtmlxgrid.js"></script> 
	<script language="JavaScript" type="text/javascript" src="dhtmlxSuite_v35/dhtmlxGrid/codebase/dhtmlxgridcell.js"></script>
	<script language="JavaScript" type="text/javascript" src="dhtmlxSuite_v35/custom/dhtmlxgrid_drag_custom.js"></script> 
	<script language="JavaScript" type="text/javascript" src="jss/ajax.js"></script> 
	<link rel="STYLESHEET" type="text/css" href="dhtmlxSuite_v35/dhtmlxGrid/codebase/dhtmlxgrid.css">
	<script language="JavaScript" type="text/javascript" src='dhtmlxSuite_v35/dhtmlxGrid/codebase/ext/dhtmlxgrid_export.js'></script>
	<link rel="stylesheet" type="text/css" href="css/catissue_suite.css">
	<link rel="stylesheet" type="text/css" href="dhtmlxSuite_v35/dhtmlxWindows/codebase/dhtmlxwindows.css">
	<link rel="stylesheet" type="text/css" href="dhtmlxSuite_v35/dhtmlxWindows/codebase/skins/dhtmlxwindows_dhx_skyblue.css">
	<script src="dhtmlxSuite_v35/dhtmlxTabbar/codebase/dhtmlxcontainer.js"></script>
	<script src="dhtmlxSuite_v35/dhtmlxWindows/codebase/dhtmlxwindows.js"></script>
	<script  src="jss/wz_tooltip.js" type="text/javascript"></script>


<style>
.a span {
    color: RED;
}
</style>

</head>
<body onload="getScanData()">
<div align="left" style="width:99%;margin-top: 10px; height:20px;margin-left:auto;margin-right:auto;" class="tr_bg_blue1">
<span class="black_ar" style="font-size: 15px; margin-left: 7px; font-weight: 600;">Container Name : </span> <span  style="font-size: 15px;" class="black_ar" id="containerName"></span>
</div>
<div id="containerGrid" style="width:99%; height:100%;margin-left:auto;margin-right:auto;margin-top: 10px;"></div>
<div id="containerPositionPopUp"></div>
</body>
</html>
<script>
function getScanData(){
var req = createRequest();
req.onreadystatechange = function() {
	  if (req.readyState != 4) return; // Not there yet
	
	  var resp = req.responseText;
	  var containerDTO = eval('('+resp+')')
	  document.getElementById('containerName').innerHTML = containerDTO.containerName;
	  validateContainerData(containerDTO);
	}

req.open("GET", "rest/ng/storage-containers/scanContainer/", false);
req.setRequestHeader("Content-Type","application/json");
req.send();

}
function validateContainerData(containerData){
	var req = createRequest();
		req.onreadystatechange = function() {
	  if (req.readyState != 4) return; // Not there yet
	
	  var resp = req.responseText;
	  var updatedContainerDTO = eval('('+resp+')')
	  init(updatedContainerDTO);
	}

	req.open("POST", "rest/ng/storage-containers/validateScanData/", false);
	req.setRequestHeader("Content-Type","application/json");
	
	/*var containerData = {
		containerName:'HeadAndNeck_WholeBloodBox_9x9',
		specimenList:[{
			sepcimenLable:'HN00001.WB.1/19/2015.3',
			containerName:'HeadAndNeck_WholeBloodBox_9x9',
			posX:'A',
			posY:'1'
		},{
			sepcimenLable:'HN00001.WB.10/21/2014.2',
			containerName:'HeadAndNeck_WholeBloodBox_9x9',
			posX:'A',
			posY:'1'
		}],
		
		oneDimensionCapacity:9,
		twoDimensionCapacity:9	
	};*/
	req.send(JSON.stringify(containerData));
}
function init(containerDTO){
	
	var columnWidth;
	var widthString = "";
	var alignString = "";
	var colType = "";
	var colSorting = "na";

	var styleArray = new Array();
	
	var dimOneArr = ['A','B','C','D'];
	var headerString = ",";
	var dimTwoArr = ['A','B','C','D'];
	var tooltips = "";
	if(containerDTO.oneDimensionCapacity>10){
		columnWidth = 80;
	}else{
		columnWidth = (100/containerDTO.oneDimensionCapacity);
	}
	widthString += columnWidth;
	alignString += "center,";
	colType += "ro,";
	colSorting += "no,";
	tooltips += "false,";
	styleArray.push("text-align:center;");
	
	for(var cnt= 0 ;cnt< containerDTO.oneDimensionCapacity;cnt++){
		widthString += columnWidth;
		alignString += "center";
		colType += "ro";
		colSorting += "no";
		styleArray.push("text-align:center;");
		headerString +=  getPositionValue(containerDTO.twoDimensionLabellingScheme,cnt+1);
		tooltips += "false";
		
		if(cnt<containerDTO.oneDimensionCapacity-1){
			widthString += ",";
			alignString += ",";
			colType += ",";
			colSorting += ",";
			headerString += ",";
			tooltips += ",";
		}
	}
	var grid = new dhtmlXGridObject("containerGrid");
	grid.setHeader(headerString,null,styleArray); 
	grid.setColAlign(alignString);
	grid.setColTypes(colType); 
	grid.setColSorting(colSorting) ;
	grid.setSkin("light");
	grid.enableAlterCss("even","uneven");
	grid.enableRowsHover(true,'grid_hover');
	grid.enableAutoHeight(true);
	grid.enableTooltips(tooltips);
	
	grid.attachEvent("onXLE", function(grid_obj,count){});

	grid.enableAutoWidth(true);
	grid.enableColumnAutoSize(true);
	grid.enableAlterCss("even","uneven");
	grid.init();
	
	//grid.parse(gridData,"json");
	for(var cnt= 0 ;cnt< containerDTO.twoDimensionCapacity;cnt++){
		var rowArr = [];
		rowArr[0] = getPositionValue(containerDTO.oneDimensionLabellingScheme,cnt+1);
		for(var cnt1 = 0; cnt1 <  containerDTO.oneDimensionCapacity; cnt1++){
			rowArr[cnt1+1] = "<img src='images/uIEnhancementImages/empty_container.gif' alt='Unused' width='32' height='32' align='middle' border='0'>";
		}
		grid.addRow(cnt,rowArr);
	}
//Specimen Type : "+occupiedPositionArr[i].type+"\nTissue Site : "+occupiedPositionArr[i].tissueSite+";
	var occupiedPositionArr = containerDTO.specimenList;
	for(var i =0 ; i < occupiedPositionArr.length;i++){
		var cellObj = grid.cells(occupiedPositionArr[i].posX-1, occupiedPositionArr[i].posY);
		var cellVal = "";
		if(!occupiedPositionArr[i].conflict){
			cellVal = "<a href='#'  id='"+occupiedPositionArr[i].sepcimenLable+"_anchor' onmouseover='addToolTip(\""+occupiedPositionArr[i].sepcimenLable+"_anchor\",\""+occupiedPositionArr[i].type+"\",\""+occupiedPositionArr[i].tissueSite+"\")'>"+occupiedPositionArr[i].sepcimenLable+"</a>";
				
		}else if(occupiedPositionArr[i].conflict){
			cellVal = "<a href='#' id='"+occupiedPositionArr[i].sepcimenLable+"_anchor' title='Error! Different specimen stored in OpenSpecimen DB.'><img src='images/uIEnhancementImages/alert.png' alt='Unused' align='middle' border='0'></img></a>";
		}
		cellObj.setValue(cellVal);
	}
	//validateContainerData();
}
var dhxWins;
function addToolTip(obj,type,tissueSite){
	document.getElementById(obj).title = "Specimen Type : "+type+"\nTissue Site : "+tissueSite;
}
function openConflictWindow(allocatedSpecimen,conflictingSpecimenLabel,posX,posY){
		if(dhxWins == undefined){
			dhxWins = new dhtmlXWindows();
			dhxWins.setSkin("dhx_skyblue");
			dhxWins.enableAutoViewport(true);
			
		}
		
		dhxWins.setImagePath("");
		if(dhxWins.window("containerPositionPopUp")==null){
			var w =350;
			var h =200;
			var x = (screen.width / 3) - (w / 2);
			var y = 0;
			dhxWins.createWindow("containerPositionPopUp", x, y, w, h);
			dhxWins.window("containerPositionPopUp").center();
			dhxWins.window("containerPositionPopUp").allowResize();
			dhxWins.window("containerPositionPopUp").setModal(true);
			dhxWins.window("containerPositionPopUp").setText("Conflicting Secimen");
			dhxWins.window("containerPositionPopUp").button("minmax1").hide();
			dhxWins.window("containerPositionPopUp").button("park").hide();
			dhxWins.window("containerPositionPopUp").button("close").hide();
			dhxWins.window("containerPositionPopUp").setIcon("images/uIEnhancementImages/alert.png", "images/uIEnhancementImages/alert.png");
			 //dhxWins.window("containerPositionPopUp").setModal(false);
			var div = document.createElement("div");
		
			div.id="popupDiv";
			div.innerHTML =  "<div style='padding-left:10px;padding-top:30px;' class='black_ar'><span style='vertical-align:2px;display:inline-block;font-weight:bold;'>Specimen from OpenSpecimen :</span><span style='vertical-align:2px;display:inline-block;'>"+allocatedSpecimen+"</span></div>"
			+"<div style='padding-left:10px;padding-top:10px;' class='black_ar'>"+
			"<span style='vertical-align:2px;display:inline-block;font-weight:bold;'>Specimen from container :</span><span style='vertical-align:2px;display:inline-block;'>"+conflictingSpecimenLabel+"</span></div>"
			+"<div style='padding-left:10px;padding-top:10px;' class='black_ar'>"+
			"<span style='vertical-align:2px;display:inline-block;font-weight:bold;'>Position :</span><span style='vertical-align:2px;display:inline-block;'>("+posX+","+posY+")</span></div>"
			+"<div style='padding-left:10px;padding-top:20px;width:100%' class='black_ar'>"+
			"<input type='button'  value='Ok' name='Ok' onClick='closeTermWindow()'style='margin-left:125px'></div>";
			document.body.appendChild(div);
			dhxWins.window("containerPositionPopUp").attachObject("popupDiv");
		}
}
function closeTermWindow(){
		dhxWins.window("containerPositionPopUp").close();
	}

function createRequest() {
  var result = null;
  if (window.XMLHttpRequest) {
    // FireFox, Safari, etc.
    result = new XMLHttpRequest();
   
  }
  else if (window.ActiveXObject) {
    // MSIE
    result = new ActiveXObject("Microsoft.XMLHTTP");
  } 
  else {
    // No known mechanism -- consider aborting the application
  }
  return result;
}


	function getPositionValue(labellingScheme,position)
	{
		var positionVal = "";
		if ("Numbers"==labellingScheme)
		{
			positionVal = position;
		}
		else if ("Alphabets Lower Case" == labellingScheme)
		{
			positionVal = integerToString(position)
					.toLowerCase();
		}
		else if ("Alphabets Upper Case" == labellingScheme)
		{
			positionVal = integerToString(position)
					.toUpperCase();
		}
		else if ("Roman Upper Case" == labellingScheme)
		{
			positionVal = integerToRoman(position)
					.toUpperCase();
		}
		else if ("Roman Lower Case" == labellingScheme)
		{
			positionVal = integerToRoman(position)
					.toLowerCase();
		}
		return positionVal;
	}

	function integerToString(columnNumber)
	{
		var i;
		var columnName = "";
		var modulo;
		while (columnNumber > 0)
		{
			modulo = (columnNumber - 1) % 26;
			i = 65 + modulo;
			columnName =  String.fromCharCode(i)+ columnName;
			columnNumber = Math.round((columnNumber - modulo) / 26);
		}
		return columnName.toLowerCase();
	}
	
	var RCODE = ["M", "CM", "D", "CD", "C", "XC",
			"L", "XL", "X", "IX", "V", "IV", "I"];
	var BVAL = [1000, 900, 500, 400, 100, 90, 50, 40,
			10, 9, 5, 4, 1];
	function integerToRoman(binary)
	{
		if (binary <= 0 || binary >= 4000)
		{
			return;
		}
		var roman = ""; // Roman notation will be accumualated here.

		// Loop from biggest value to smallest, successively subtracting,
		// from the binary value while adding to the roman representation.
		for (var i = 0; i < RCODE.length; i++)
		{
			while (binary >= BVAL[i])
			{
				binary -= BVAL[i];
				roman += RCODE[i];
			}
		}
		return roman.toLowerCase();
	}





</script>