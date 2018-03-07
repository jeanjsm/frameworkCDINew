
function somenteNumero(evento) {
	var tecla = (window.event) ? event.keyCode : evento.which;

	// alert("key pressed: " + tecla);

	if (tecla == 0)
		return true;

	if ((tecla > 47 && tecla < 58))
		return true;
	else {
		if (tecla == 8)
			return true;
		else
			return false;
	}
}

function toLowerCase(el) {
	el.value = el.value.toLowerCase();
}

function toUpperCase(el) {
	el.value = el.value.toUpperCase();
}
