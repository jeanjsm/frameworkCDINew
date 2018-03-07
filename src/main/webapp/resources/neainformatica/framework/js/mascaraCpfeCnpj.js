function mascara(str) {
	// Caso passe de 14 caracteres será formatado como CNPJ
	if (str.value.length > 14)
		str.value = cnpj(str.value);
	// Caso contrário como CPF
	else if (str.value.length <= 14)
		str.value = cpf(str.value);
}

// Funcao de formatacao CPF
function cpf(valor) {
	// Remove qualquer caracter digitado que não seja numero
	valor = valor.replace(/\D/g, "");

	// Adiciona um ponto entre o terceiro e o quarto digito
	valor = valor.replace(/(\d{3})(\d)/, "$1.$2");

	// Adiciona um ponto entre o terceiro e o quarto dígitos
	// desta vez para o segundo bloco
	valor = valor.replace(/(\d{3})(\d)/, "$1.$2");

	// Adiciona um hifen entre o terceiro e o quarto dígitos
	valor = valor.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
	return valor;
}

// Funcao de formatacao CNPJ
function cnpj(valor) {
	// Remove qualquer caracter digitado que não seja numero
	valor = valor.replace(/\D/g, "");

	// Adiciona um ponto entre o segundo e o terceiro dígitos
	valor = valor.replace(/^(\d{2})(\d)/, "$1.$2");

	// Adiciona um ponto entre o quinto e o sexto dígitos
	valor = valor.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3");

	// Adiciona uma barra entre o oitavaloro e o nono dígitos
	valor = valor.replace(/\.(\d{3})(\d)/, ".$1/$2");

	// Adiciona um hífen depois do bloco de quatro dígitos
	valor = valor.replace(/(\d{4})(\d)/, "$1-$2");
	return valor;
}