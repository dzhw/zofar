function deselectInputs(name) {
	elements = document.getElementsByName(name);
	if (elements != null) {
		for (var i = 0; i < elements.length; i++) {
			elements[i].checked = false;
		}
	}
}

function deselectSelect(id) {
	select = document.getElementById(id);
	if (select != null) {
		select.selectedIndex = 0;
	}
}

function clearTextField(id) {
	textField = document.getElementById(id);
	if (textField != null) {
		textField.value = "";
	}
}