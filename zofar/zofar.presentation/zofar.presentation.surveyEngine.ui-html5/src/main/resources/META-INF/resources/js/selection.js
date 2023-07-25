
var zofar_singleChoice_openMap = {};
var zofar_singleChoice_matrixMap = {};
var zofar_multipleChoice_exclusiveMap = {};
var zofar_multipleChoice_notexclusiveMap = {};

var zofar_multipleChoice_attached_exclusiveMap = {};
var zofar_multipleChoice_attached_notexclusiveMap = {};

var zofar_multipleChoice_reverse_attached_exclusiveMap = {};
var zofar_multipleChoice_reverse_attached_notexclusiveMap = {};


function zofar_singleChoice_triggerRadio(qid, rid) {
	if (qid in zofar_singleChoice_openMap) {
		temp = zofar_singleChoice_openMap[qid];
		var index;
		for (index = 0; index < temp.length; ++index) {
			var oid = temp[index];
			if (oid.indexOf(rid) === -1) {
				var openObj = document.getElementById(oid);
				openObj.value = "";
			}
		}
	}
}

function zofar_matrix_singleChoice_triggerRadio(pid,qid, rid) {
	if (pid in zofar_singleChoice_openMap) {
		temp = zofar_singleChoice_openMap[pid];
		var index;
		for (index = 0; index < temp.length; ++index) {
			var oid = temp[index];
			var oid1 = null;
			if(qid in zofar_singleChoice_openMap)oid1 = zofar_singleChoice_openMap[qid][0];
			if ((oid1 == null)||(oid.indexOf(oid1) === -1)) {
				var openObj = document.getElementById(oid);
				openObj.value = "";
			}
		}
	}
}

function zofar_multipleChoice_triggerCheck(iid, oid) {
	var inputObj = document.getElementById(iid);
	if (!inputObj.checked) {
		var openObj = document.getElementById(oid);
		openObj.value = "";
	}
}

function zofar_multipleChoice_triggerExclusive(iid, qid) {
	var inputObj = document.getElementById(iid);
	if (inputObj.checked) {
		// uncheck all other items
		if (qid in zofar_multipleChoice_exclusiveMap) {
			temp = zofar_multipleChoice_exclusiveMap[qid];
			var index;
			for (index = 0; index < temp.length; ++index) {
				var oid = temp[index];
				if(oid == iid)continue;
				var oObj = document.getElementById(oid);
				//oObj.checked = false;
				if(oObj.checked)oObj.click();
			}
		}
		if (qid in zofar_multipleChoice_attached_exclusiveMap) {
			temp = zofar_multipleChoice_attached_exclusiveMap[qid];
			var index;
			for (index = 0; index < temp.length; ++index) {
				var oid = temp[index];
				console.log(oid);
//				if(oid == iid)continue;
				var oObj = document.getElementById(oid);
				if(oObj != null)oObj.value = '';
			}
		}
		if (qid in zofar_multipleChoice_notexclusiveMap) {
			temp = zofar_multipleChoice_notexclusiveMap[qid];
			var index;
			for (index = 0; index < temp.length; ++index) {
				var oid = temp[index];
				if(oid == iid)continue;
				var oObj = document.getElementById(oid);
				//oObj.checked = false;
				if(oObj.checked)oObj.click();
			}
		}
	}
}

function zofar_multipleChoice_triggerNotExclusive(iid, qid) {
//	alert("not exclusive "+iid+" , "+qid);
	var inputObj = document.getElementById(iid);
	if (inputObj.checked) {
//		// uncheck all other items
		if (qid in zofar_multipleChoice_notexclusiveMap) {
			temp = zofar_multipleChoice_notexclusiveMap[qid];
			var index;
			for (index = 0; index < temp.length; ++index) {
				var oid = temp[index];
				if(oid == iid)continue;
				var oObj = document.getElementById(oid);
				//oObj.checked = false;
				if(oObj.checked)oObj.click();
			}
		}
//		if (qid in zofar_multipleChoice_exclusiveMap) {
//			temp = zofar_multipleChoice_exclusiveMap[qid];
//			var index;
//			for (index = 0; index < temp.length; ++index) {
//				var oid = temp[index];
//				if(oid == iid)continue;
//				var oObj = document.getElementById(oid);
//				//oObj.checked = false;
//				if(oObj.checked)oObj.click();
//			}
//		}
		var combinedId = qid+"_"+iid;
		if (qid in zofar_multipleChoice_attached_notexclusiveMap) {
			temp = zofar_multipleChoice_attached_notexclusiveMap[qid];
			var index;
			for (index = 0; index < temp.length; ++index) {
				var oid = temp[index];
//				if(oid == iid)continue;
				var oObj = document.getElementById(oid);
				oObj.value = '';
			}
		}
	}
}

function zofar_triggerOpen(iid, oid) {
	console.log("triggerOpen "+iid+" , "+oid);
	var inputObj = document.getElementById(iid);
	if(inputObj == null)inputObj = document.getElementById(zofar_singleChoice_matrixMap[iid]);
//	inputObj.checked = true;
	if(!inputObj.checked)inputObj.click();
	if (inputObj.type == "radio") {
		zofar_singleChoice_triggerRadio(inputObj.name, iid);
	}
	else if (inputObj.type == "checkbox") {
		console.log("exclusive check");
		if ((zofar_multipleChoice_reverse_attached_exclusiveMap != null)&&(oid in zofar_multipleChoice_reverse_attached_exclusiveMap)) {
			zofar_multipleChoice_triggerNotExclusive(iid, zofar_multipleChoice_reverse_attached_exclusiveMap[oid]);
		}
		else if ((zofar_multipleChoice_reverse_attached_notexclusiveMap != null)&&(oid in zofar_multipleChoice_reverse_attached_notexclusiveMap)) {
			zofar_multipleChoice_triggerExclusive(iid, zofar_multipleChoice_reverse_attached_notexclusiveMap[oid]);
		}
	}
}

function zofar_matrix_singleChoice_register_open(pid,qid,rid,oid) {
	var temp;
	if (qid in zofar_singleChoice_matrixMap) {
		temp = zofar_singleChoice_matrixMap[qid];
	} else {
		temp = new Array();
	}
	temp.push(rid);
	zofar_singleChoice_matrixMap[qid] = temp;
	zofar_singleChoice_register_open(pid,oid);
	zofar_singleChoice_register_open(qid,oid);
}

function zofar_singleChoice_register_open(qid, oid) {
	var temp;
	if (qid in zofar_singleChoice_openMap) {
		temp = zofar_singleChoice_openMap[qid];
	} else {
		temp = new Array();
	}
	temp.push(oid);
	zofar_singleChoice_openMap[qid] = temp;
	//alert("register "+qid+" , "+zofar_singleChoice_openMap[qid]);
}

function zofar_multipleChoice_register_exclusive(qid, oid,exclusive) {
//	alert("register exclusive "+qid+" , "+oid+" , "+exclusive);
	
	if(exclusive){
		var temp;
		if (qid in zofar_multipleChoice_notexclusiveMap) {
			temp = zofar_multipleChoice_notexclusiveMap[qid];
		} else {
			temp = new Array();
		}
		temp.push(oid);
		zofar_multipleChoice_notexclusiveMap[qid] = temp;
	}
	else{
		var temp;
		if (qid in zofar_multipleChoice_exclusiveMap) {
			temp = zofar_multipleChoice_exclusiveMap[qid];
		} else {
			temp = new Array();
		}
		temp.push(oid);
		zofar_multipleChoice_exclusiveMap[qid] = temp;
	}
}

function zofar_multipleChoice_register_exclusive_attached(qid, oid, attachedId,exclusive) {
	console.log("zofar_multipleChoice_register_exclusive_attached "+qid+" , "+oid+" , "+attachedId+" , "+exclusive);
	
	if(exclusive){
		var temp;
		if (qid in zofar_multipleChoice_attached_notexclusiveMap) {
			temp = zofar_multipleChoice_attached_notexclusiveMap[qid];
		} else {
			temp = new Array();
		}
		temp.push(attachedId);
		zofar_multipleChoice_attached_notexclusiveMap[qid] = temp;
		zofar_multipleChoice_reverse_attached_notexclusiveMap[attachedId] = qid;
	}
	else{
		var temp;
		if (qid in zofar_multipleChoice_attached_exclusiveMap) {
			temp = zofar_multipleChoice_attached_exclusiveMap[qid];
		} else {
			temp = new Array();
		}
		temp.push(attachedId);
		zofar_multipleChoice_attached_exclusiveMap[qid] = temp;
		zofar_multipleChoice_reverse_attached_exclusiveMap[attachedId] = qid;
	}
}