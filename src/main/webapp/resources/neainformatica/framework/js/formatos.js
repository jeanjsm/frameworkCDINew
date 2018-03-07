// Seta cookies
function setCookie(c_name,value,expiredays)
{
	var exdate=new Date()
	exdate.setDate(exdate.getDate()+expiredays)
	document.cookie=c_name+ "=" +escape(value)+((expiredays==null) ? "" : ";expires="+exdate.toGMTString())+";path=/aapf/";
}	

// Recupera cookies
function getCookie(c_name)
{
	if (document.cookie.length>0)
	  {
	  c_start=document.cookie.indexOf(c_name + "=")
	  if (c_start!=-1)
	    { 
	    c_start=c_start + c_name.length+1 
	    c_end=document.cookie.indexOf(";",c_start)
	    if (c_end==-1) c_end=document.cookie.length
	    return unescape(document.cookie.substring(c_start,c_end))
	    } 
	  }
	return ""
}	

// Reseta o o cookie para abrir o menu no Acesso Rápido
function iniciaAcessoRapido(){
	setCookie("idSubMenu","4",1);
	setCookie("idSubMenuTrans","1",1);
}

function getMensagemErro()
{
	return mensagemErro;
}

// Redireciona o cliente para URL do banner
// Caso URL = https, mantenho-o na mesma janela;
// Caso URL = http, abro nova janela chamada Portal.
function redirecionamentoInteligente(){
	url = document.getElementById('bannerLinkId').href;
	if (url.indexOf("https")>-1){
		location.href = url;
	} else {
		window.open( url , 'Portal');
	}
}

// Javascript para impedir duplo clique
var submeteuFormulario = 0;
function controleDuploClique() {
	if(submeteuFormulario=="1") {
		return false;
	}
	submeteuFormulario = "1";
	return true;
}

// Abre janela de ajuda
function janelaAjuda(url)
{
	janela = window.open( url , 'Ajuda', 'height=500,width=600,status=yes,toolbar=no,menubar=no,location=no,resizable=no,scrollbars=yes');
	if (window.focus) {
		janela.focus();
	}
}

// Script que mostra ou esconde um elemento
function mostraEsconde(layer) {
	if (document.getElementById(layer).style.display != "none") {
		document.getElementById(layer).style.display = "none";
	} else {
		document.getElementById(layer).style.display = "";
	}
}

// Script que esconde um elemento
function escondeLayer(layer) {
	document.getElementById(layer).style.display="none";
}

// Script que mostra um elemento
function mostraLayer(layer) {
	document.getElementById(layer).style.display="";
}

// Script que abre popup
var janela;
function popup(url, target, features) {
	newwindow = window.open(url, target, features);
	if (window.focus) {
		newwindow.focus();
	}
	return false;
}

function popupAlertaRedirecionamento(url, target, features) {
	newwindow = window.open(url, target, features);
	if (window.focus) {
		newwindow.focus();
	}
	// return false;
}

// Formata o campo CEP
function formataCEP(campo){
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;

	if ( tam <= 3 )
		campo.value = vr;
	if ( tam > 3 ) 
		campo.value = vr.substr(0, tam-3 ) + '-' + vr.substr(tam-3, tam);
}

// Limpa os campos da tela
function limpa(){
	var elements = document.forms.aapf.elements;
	for (i = 0; i < elements.length; i++) {
		if (elements[i].type == 'text' || elements[i].type == 'text-area'
				|| elements[i].type == 'select-one' || elements[i].type == 'radio'
				|| elements[i].type == 'checkbox' || elements[i].type == 'password'
				|| elements[i].type == 'textarea' || elements[i].type == 'tel' || elements[i].type == 'number'){    
			elements[i].value='';
		}
	}
	if(document.tclJava)
	{
		document.tclJava.limpa();
	}
}
// Limpa os campos da tela
function limpaTodosMenos(nome1, nome2, nome3){
	var elements = document.forms.aapf.elements;
	for (i = 0; i < elements.length; i++) {
		if(elements[i].name != nome1 && elements[i].name != nome2 && elements[i].name != nome3){
			if (elements[i].type == 'text' || elements[i].type == 'text-area'
					|| elements[i].type == 'select-one' || elements[i].type == 'radio'
					|| elements[i].type == 'checkbox' || elements[i].type == 'password'
					|| elements[i].type == 'textarea' || elements[i].type == 'tel'){
				elements[i].value='';
			}
		}
	}
	if(document.tclJava)
	{
		document.tclJava.limpa();
	}
}

function limpaEspecifico(text,textarea,selectone,radio,checkbox,password,submit){

	var elements = document.forms.aapf.elements;
	
	for (i = 0; i < elements.length; i++) {
	
		if(text == 'true'){
			if(elements[i].type == 'text'){
				elements[i].value='';
			}
		}
		if(textarea == 'true'){
			if(elements[i].type == 'text-area'){
				elements[i].value='';
			}
		}
		if(selectone == 'true'){
			if(elements[i].type == 'select-one'){
				elements[i].value='';
			}
		}
		if(radio == 'true'){
			if(elements[i].type == 'radio'){
				elements[i].value='';
			}
		}
		if(checkbox == 'true'){
			if(elements[i].type == 'checkbox'){
				elements[i].value='';
			}
		}
		if(password == 'true'){
			if(elements[i].type == 'password'){
				elements[i].value='';
			}
		}
	}
}

function limpaEspecificoPoupanca(text,textarea,selectone,radio,checkbox,password,submit){
 	var elements = document.forms.aapf.elements;

	for (i = 0; i < elements.length; i++) {
  
		if(text == 'true'){
			  
			if(elements[i].type == 'text'){
		 		
		 		if(elements[i].name != 'dataPagamento')
				{	
				   elements[i].value='';
				}   
			}
		}
		if(textarea == 'true'){
			if(elements[i].type == 'text-area'){
				elements[i].value='';
			}
		}
		if(selectone == 'true'){
			if(elements[i].type == 'select-one'){
				elements[i].value='';
			}
		}
		if(radio == 'true'){
			if(elements[i].type == 'radio'){
				elements[i].value='';
			}
		}
		if(checkbox == 'true'){
			if(elements[i].type == 'checkbox'){
				elements[i].checked=false;
			}
		}
		if(password == 'true'){
			if(elements[i].type == 'password'){
				elements[i].value='';
			}
		}
	}
}


// Formata o campo Agencia
function formataAgenciaConta(campo){
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;
	if ( tam <= 1 )
		campo.value = vr;
	if ( tam > 1 ) 
		campo.value = vr.substr(0, tam-1 ) + '-' + vr.substr(tam-1, tam); 
}

// Formata data no padr?o DDMMAAAA
function formataData(campo){
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;

	if ( tam > 2 && tam < 5 )
		campo.value = vr.substr( 0, tam - 2  ) + '/' + vr.substr( tam - 2, tam );
	if ( tam >= 5 && tam <= 10 )
		campo.value = vr.substr( 0, 2 ) + '/' + vr.substr( 2, 2 ) + '/' + vr.substr( 4, 4 ); 

}

// Formata hora no padrao HH:MM
function formataHora(campo,teclapres) {
	var tecla = teclapres.keyCode;
	campo.value = filtraCampo(campo);
	vr = campo.value;
	vr = vr.replace( ".", "" );
	vr = vr.replace( ":", "" );
	vr = vr.replace( ":", "" );
	tam = vr.length + 1;

	if ( tecla != 9 && tecla != 8 ){
		if ( tam > 2 && tam < 5 )
			campo.value = vr.substr( 0, tam - 2  ) + ':' + vr.substr( tam - 2, tam );
	}
}

// Formata o campo valor
function formataValor(campo) {
	campo.value = filtraCampoValor(campo); 
	tam = vr.length;
	
	if (tam > 2) {
		campo.value = campo.value.substr(0, campo.value.length - 2) + ',' + campo.value.substr(campo.value.length - 2, campo.value.length);
	}
	
 	if (tam > 6) {
 		campo.value = campo.value.substr(0, campo.value.length - 6) + '.' + campo.value.substr(campo.value.length - 6, campo.value.length);
 	}
 	
 	if (tam > 10) {
 		campo.value = campo.value.substr(0, campo.value.length - 10) + '.' + campo.value.substr(campo.value.length - 10, campo.value.length);
 	}
 	
 	if (tam > 14) {
 		campo.value = campo.value.substr(0, campo.value.length - 14) + '.' + campo.value.substr(campo.value.length - 14, campo.value.length);
 	}
 	
 	if (tam > 18) {
 		campo.value = campo.value.substr(0, campo.value.length - 18) + '.' + campo.value.substr(campo.value.length - 18, campo.value.length);
 	}
 	
 	if (campo.value.substr(0, 1) == '.' || campo.value.substr(0, 1) == ',') {
 		campo.value = campo.value.substr(1, campo.value.length);
 	}
}

//Formata o campo valor apenas na exibição
function formataValorExibicao(campo) {
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;

	if ( tam == 1 ){
		campo.value = '0,0' + vr; }
	if ( tam == 2 ){
		campo.value = '0,' + vr; }
 	if ( (tam > 2) && (tam <= 5) ){
 		campo.value = vr.substr( 0, tam - 2 ) + ',' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 6) && (tam <= 8) ){
 		campo.value = vr.substr( 0, tam - 5 ) + '.' + vr.substr( tam - 5, 3 ) + ',' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 9) && (tam <= 11) ){
 		campo.value = vr.substr( 0, tam - 8 ) + '.' + vr.substr( tam - 8, 3 ) + '.' + vr.substr( tam - 5, 3 ) + ',' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 12) && (tam <= 14) ){
 		campo.value = vr.substr( 0, tam - 11 ) + '.' + vr.substr( tam - 11, 3 ) + '.' + vr.substr( tam - 8, 3 ) + '.' + vr.substr( tam - 5, 3 ) + ',' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 15) && (tam <= 18) ){
 		campo.value = vr.substr( 0, tam - 14 ) + '.' + vr.substr( tam - 14, 3 ) + '.' + vr.substr( tam - 11, 3 ) + '.' + vr.substr( tam - 8, 3 ) + '.' + vr.substr( tam - 5, 3 ) + ',' + vr.substr( tam - 2, tam ) ;}
}

// Formata o campo valor
function formataNumerico(campo) {

	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;
}

// limpa todos os caracteres especiais do campo solicitado
function filtraCampo(campo){
	var s = "";
	var cp = "";
	vr = campo.value;
	tam = vr.length;
	for (i = 0; i < tam ; i++) {  
		if (vr.substring(i,i + 1) != "/" && vr.substring(i,i + 1) != "-" && vr.substring(i,i + 1) != "."  && vr.substring(i,i + 1) != "," ){
		 	s = s + vr.substring(i,i + 1);}
	}
	campo.value = s;
	return cp = campo.value
}
//limpa todos os caracteres especiais do campo solicitado
function filtraCampoValor(campo){
	var s = "";
	var cp = "";
	vr = campo.value;
	tam = vr.length;
	for (i = 0; i < tam ; i++) {  
		if (vr.substring(i,i + 1) >= "0" && vr.substring(i,i + 1) <= "9"){
		 	s = s + vr.substring(i,i + 1);}
	} 
	campo.value = s;
	return cp = campo.value
}
		
// Seta o ajuda do campo no campo <SPAN>
function setaTextoAjuda(txt) {
	if(document.getElementById('textoAjuda')) document.getElementById('textoAjuda').innerHTML = txt + '&nbsp;' ;
}



function getTeclaPressionada(evt)
{
	if(typeof(evt)=='undefined')
		evt = window.event;
	return(evt.keyCode ? evt.keyCode : (evt.which ? evt.which : evt.charCode));
}

// teclas 63230 a 63240 = safari
function isTeclaEspecial(key)
{
	return key<32||(key>=35&&key<=36)||(key>=37&&key<=40)||key==46||(key>=63230&&key<=63240);
}

function isTeclaRelevante(key)
{
	return (key == 8)||(key == 46)||(key == 88)||(key>=48&&key<=57)||(key>=96&&key<=105);
}

function isCaracterRelevante(key)
{
	return (key == 88)||(key == 120)||(key>=48&&key<=57);
}

function isCopiaCola(ctrlKey, key)
{
	return ctrlKey && (key == 118 || key == 86 || key == 99 || key == 67);
}


function filtraTeclas(evt)
{
	var key = getTeclaPressionada(evt);
	if(isTeclaEspecial(key) || isTeclaRelevante(key) || isCopiaCola(evt.ctrlKey, key))
		return true;
	StopEvent(evt);
	return false;
}

function filtraCaracteres(evt)
{
	var key = getTeclaPressionada(evt);
	if(isTeclaEspecial(key) || isCaracterRelevante(key) || isCopiaCola(evt.ctrlKey, key))
		return true;
	StopEvent(evt);
	return false;
}

function isTeclaNumerica(key) {
	return ((key > 47 && key < 58) || (key > 95 && key < 106));
}

function isTeclaNumericaIPad(key) {
	return ((key > 47 && key < 58));
}

function asciiToNumber(code) {
	var retorno = "";
	switch (code)
	{
	   case 48 :
		   retorno = "0";
		   break;
	   case 49 :
		   retorno = "1";
		   break;
	   case 50 :
		   retorno = "2";
		   break;
	   case 51 :
		   retorno = "3";
		   break;
	   case 52 :
		   retorno = "4";
		   break;
	   case 53 :
		   retorno = "5";
		   break;
	   case 54 :
		   retorno = "6";
		   break;
	   case 55 :
		   retorno = "7";
		   break;
	   case 56 :
		   retorno = "8";
		   break;
	   case 57 :
		   retorno = "9";
		   break;
	   case 96 :
		   retorno = "0";
		   break;
	   case 97 :
		   retorno = "1";
		   break;
	   case 98 :
		   retorno = "2";
		   break;
	   case 99 :
		   retorno = "3";
		   break;
	   case 100 :
		   retorno = "4";
		   break;
	   case 101 :
		   retorno = "5";
		   break;
	   case 102 :
		   retorno = "6";
		   break;
	   case 103 :
		   retorno = "7";
		   break;
	   case 104 :
		   retorno = "8";
		   break;
	   case 105 :
		   retorno = "9";
		   break;		   
	} 
	return retorno;
}

function removerSubString(str,posicaoInicial,posicaoFinal){
	if(posicaoInicial == posicaoFinal){
		if( posicaoInicial == 1){
			return str.substring(1);
		}else if(posicaoInicial == str.length ){
			return str.substring(0,posicaoInicial-1);
		}else{
			return str.substring(0,posicaoInicial-1) + str.substring(posicaoInicial);
		}		
	}else{
		if((posicaoFinal - posicaoInicial) == str.length){
			return("");
		}else{
			return(str.substring(0,posicaoInicial) + str.substring(posicaoFinal));
		}
	}
}

function setCaretTo(obj, poIni, posFim) {
	if(obj.setSelectionRange){
		obj.focus();
		obj.setSelectionRange(poIni,posFim);
    }else if (obj.createTextRange){
        var range = obj.createTextRange();
        range.collapse(true);
        range.moveEnd('character', posFim);
        range.moveStart('character', poIni);
        range.select();
    }

}

function getSelectionStart(o) {
	if (o.createTextRange) {
		var r = document.selection.createRange().duplicate()
		r.moveEnd('character', o.value.length)
		if (r.text == '') return o.value.length
		return o.value.lastIndexOf(r.text)
	} else return o.selectionStart
}

function getSelectionEnd(o) {
	if (o.createTextRange) {
		var r = document.selection.createRange().duplicate()
		r.moveStart('character', -o.value.length)
		return r.text.length
	} else return o.selectionEnd
}

function filtraPassa(field, evt, idNome) {

	var key = getTeclaPressionada(evt);
	var idVar = document.getElementById(idNome);
	var posIni = getSelectionStart(field);
	var posFim = getSelectionEnd(field);
	if (idVar == null) {
		// Criar elemento
		var div = document.getElementById("retrancas");
		div.innerHTML = "<input type='hidden' name='"+idNome+"' id='"+idNome+"' value='' maxlenght='8' />";
		idVar = document.getElementById(idNome);
	}
	if(key==13 || key==9){
		return true;
	}else if(isTeclaEspecial(key)){
		if((key >= 35 && key <= 40) || key == 16 ){
			return true;
		}
		if(key == 8){
			idVar.value = removerSubString(idVar.value,posIni,posFim);
			return true;
		}else if(key == 46){
			if(posIni ==posFim){
				idVar.value = removerSubString(idVar.value,posIni+1,posIni+1);
			}else{
				idVar.value = removerSubString(idVar.value,posIni,posFim);
			}
			return true;
		}
		StopEvent(evt);
		return false;		

	}else if ((!isTeclaNumerica(key)) || ((field.value.length == 8) &&  (posIni ==posFim))) {
		StopEvent(evt);
		return false;
	}else {
		
		// teclas numéricas, dentro do limite do campo
		var randomnumber=Math.floor(Math.random()*10);
		var digito = asciiToNumber(key);

		if(posIni ==posFim){
			idVar.value = idVar.value.substring(0,posIni) + digito + idVar.value.substring(posIni,idVar.value.length);
			field.value = field.value.substring(0,posIni) + randomnumber + field.value.substring(posIni,field.value.length);
		}else{
			var valorfield1 = idVar.value;
			var valorfield2 = field.value;
			idVar.value = removerSubString(idVar.value,posIni,posFim);
			field.value = removerSubString(field.value,posIni,posFim);
			idVar.value = valorfield1.substring(0,posIni) + digito + valorfield1.substring(posFim);
			field.value = valorfield2.substring(0,posIni) + randomnumber + valorfield2.substring(posFim);
		}
		setCaretTo(field,posIni+1,posIni+1);  
		StopEvent(evt);   
		return false;
	}
}

function filtraTecla(field, evt) {
	
	var key = getTeclaPressionada(evt);
	if(!isTeclaNumerica(key)){
		StopEvent(evt);
		return false;		
	} 
}
function filtraPassaIpad(field, evt, idNome) {
	
	
	var key = getTeclaPressionada(evt);
	 
	var idVar = document.getElementById(idNome);
	var posIni = getSelectionStart(field);
	var posFim = getSelectionEnd(field);
	 
	if (idVar == null) {
		// Criar elemento
		var div = document.getElementById("retrancas");
		div.innerHTML = "<input type='hidden' name='"+idNome+"' id='"+idNome+"' value='' maxlenght='8' />";
		idVar = document.getElementById(idNome);
	}
	if(key == 8){
		idVar.value = removerSubString(idVar.value,posIni,posFim);
		return true;
	}else if(key==13 || key==9 ){  
		return true;
	}else if(isTeclaEspecial(key)){
		StopEvent(evt); 
		return false;		
	}else if ((!isTeclaNumericaIPad(key)) || ((field.value.length == 8) &&  (posIni ==posFim))) {
		StopEvent(evt); 
		return false; 
	}else {
		// teclas numéricas, dentro do limite do campo
		
		// No caso do Ipad, estamos usando campo numérico, por isso, todos os
		// caracteres devem ser substituidos por "*"
		var randomnumber="*"; // Math.floor(Math.random()*10);
		var digito = asciiToNumber(key);

		if(posIni ==posFim){
			idVar.value = idVar.value.substring(0,posIni) + digito + idVar.value.substring(posIni,idVar.value.length);
			field.value = field.value.substring(0,posIni) + randomnumber + field.value.substring(posIni,field.value.length);
		}else{
			var valorfield1 = idVar.value;
			var valorfield2 = field.value;
			idVar.value = removerSubString(idVar.value,posIni,posFim);
			field.value = removerSubString(field.value,posIni,posFim);
			idVar.value = valorfield1.substring(0,posIni) + digito + valorfield1.substring(posFim);
			field.value = valorfield2.substring(0,posIni) + randomnumber + valorfield2.substring(posFim);
		}
		setCaretTo(field,posIni+1,posIni+1);  
		StopEvent(evt);   
		return false;
	}
}  
function filtraPassaIpad6(field, evt, idNome) {

	var key = getTeclaPressionada(evt);
	var idVar = document.getElementById(idNome);
	var posIni = getSelectionStart(field);
	var posFim = getSelectionEnd(field);
	if (idVar == null) {
		// Criar elemento
		var div = document.getElementById("retrancas");
		div.innerHTML = "<input type='hidden' name='"+idNome+"' id='"+idNome+"' value='' maxlenght='6' />";
		idVar = document.getElementById(idNome);
	}
	if(key==13 || key==9){
		return true;
	}else if(isTeclaEspecial(key)){
		if((key >= 35 && key <= 40) || key == 16 ){
			return true;
		}
		if(key == 8){
			idVar.value = removerSubString(idVar.value,posIni,posFim);
			return true;
		}else if(key == 46){
			if(posIni ==posFim){
				idVar.value = removerSubString(idVar.value,posIni+1,posIni+1);
			}else{
				idVar.value = removerSubString(idVar.value,posIni,posFim);
			}
			return true;
		}
		StopEvent(evt);
		return false;		

	}else if ((!isTeclaNumerica(key)) || ((field.value.length == 6) &&  (posIni ==posFim))) {
		StopEvent(evt);
		return false;
	}else {
		
		// teclas numéricas, dentro do limite do campo
		
		// No caso do Ipad, estamos usando campo numérico, por isso, todos os
		// caracteres devem ser substituidos por "*"
		var randomnumber="*"; // Math.floor(Math.random()*10);
		var digito = asciiToNumber(key);

		if(posIni ==posFim){
			idVar.value = idVar.value.substring(0,posIni) + digito + idVar.value.substring(posIni,idVar.value.length);
			field.value = field.value.substring(0,posIni) + randomnumber + field.value.substring(posIni,field.value.length);
		}else{
			var valorfield1 = idVar.value;
			var valorfield2 = field.value;
			idVar.value = removerSubString(idVar.value,posIni,posFim);
			field.value = removerSubString(field.value,posIni,posFim);
			idVar.value = valorfield1.substring(0,posIni) + digito + valorfield1.substring(posFim);
			field.value = valorfield2.substring(0,posIni) + randomnumber + valorfield2.substring(posFim);
		}
		setCaretTo(field,posIni+1,posIni+1);  
		StopEvent(evt);   
		return false;
	}
}

function StopEvent(evt)
{
	if(document.all)evt.returnValue=false;
	else if(evt.preventDefault)evt.preventDefault();
}



function formataMascara(format, field)
{
	var result = "";
	var maskIdx = format.length - 1;
	var error = false;
	var valor = field.value;
	var posFinal = false;
	if( field.setSelectionRange ) 
	{
    	if(field.selectionStart == valor.length)
    		posFinal = true;
    }
	valor = valor.replace(/[^0123456789Xx]/g,'')
	for (var valIdx = valor.length - 1; valIdx >= 0 && maskIdx >= 0; --maskIdx)
	{
		var chr = valor.charAt(valIdx);
		var chrMask = format.charAt(maskIdx);
		switch (chrMask)
		{
		case '#':
			if(!(/\d/.test(chr)))
				error = true;
			result = chr + result;
			--valIdx;
			break;
		case '@':
			result = chr + result;
			--valIdx;
			break;
		default:
			result = chrMask + result;
		}
	}

	field.value = result;
	field.style.color = error ? 'red' : '';
	if(posFinal)
	{
		field.selectionStart = result.length;
		field.selectionEnd = result.length;
	}
	return result;
}

function saltaCampo(campo,tamanhoMaximo,indice,evt){
	var vr = campo.value;
	var tam = vr.length;
	var elements = document.forms.aapf.elements;
	if (tam>=tamanhoMaximo && typeof(elements[indice])!='undefined'){
		// elements[indice].focus();
		for (i=0;i<elements.length;i++) {
			if (elements[i].tabIndex==indice+1){
				elements[i].focus();
			}
		}
	}
}



function trocaBotaoAction(botao)
{
	if ( document.applets['tclAssinador'] )
	{
		pos = document.forms.aapf.action.indexOf("?");
		acao = document.forms.aapf.action;
		if(pos != -1)
		{
			acao = acao.substring(0, pos);
		}
		document.forms.aapf.action = acao + "?" + botao + ".x=1";
	}
}

var confirmaAssinador = 0;
var linkJS = "";

function getSenha() {
	if ( document.getElementById('tclAssinadorContent') && document.getElementById('tclAssinadorContent').style.display == 'none' )
	{
		if(showApplet());
			return false;
	}
	if ( document.applets['tclJava'] ){
		var senha = document.applets['tclJava'].getSenha();
		document.forms.aapf.elements['senhaConta'].value = senha;
	}
	else if ( document.applets['tclAssinador'] )
	{
		if(confirmaAssinador == 1)
		{
			confirmaAssinador = 0;
			if( document.applets['tclAssinador'].ok() )
			{
				loadData();
				return true;
			}	
			else
			{
				document.applets['tclAssinador'].focus();
				return false;
			}
		}
		else
		{
			return document.applets['tclAssinador'].cancel();
		}
	}
	else if ( document.getElementById('senhaConta_') )
	{
		document.getElementById('senhaConta').value = document.getElementById('senhaConta_').value; 
	}
	return true;
}
function getSenhaGBAS(campo,idCh,idDg,idDg2){
	document.getElementById(campo).value = document.applets['GbPluginObj'].Encripta(document.getElementById(campo).value,idCh);
}

function getSenha2(campo,idCh,idDg,idDg2){
	if ( document.getElementById(campo) ){
		GbPluginObj.Digest(idDg);
		GbPluginObj.Digest2(idDg2);
		document.getElementById(campo).value = GbPluginObj.Function8(document.getElementById(campo).value,idCh);
 	}
	return true;	
}

function getSenhaIP(campo,idCh,idDg,idDg2){
	return true;
}

function trocaBotaoAction(botao)
{
	if ( document.applets['tclAssinador'] )
	{
		pos = document.forms.aapf.action.indexOf("?");
		acao = document.forms.aapf.action;
		if(pos != -1)
		{
			acao = acao.substring(0, pos);
		}
		document.forms.aapf.action = acao + "?" + botao + ".x=1";
	}
}

// metodo chamado pela applet na finaliza??o do processo
function appletTerminated(ok)
{
	if (ok)
		loadData();
	if (linkJS == "")
		document.forms.aapf.submit();
	else if (linkJS == "retorna") {
		linkJS = "";
		window.history.back(1);
	}
	else {
		var linkJSTemp = linkJS;
		linkJS = "";
		window.navigate(linkJSTemp);
	}
}


function formataMesAno(campo){
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;

	if ( tam > 2 && tam < 5 )
		campo.value = vr.substr( 0, tam - 2  ) + '/' + vr.substr( tam - 2, tam );
	if ( tam >= 5 && tam <= 10 )
		campo.value = vr.substr( 0, 2 ) + '/' + vr.substr( 2, 4 ); 
}

function formataCgc(campo) {
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;

	if ( tam <= 2 ){ 
 		campo.value = vr ; }
 	if ( (tam > 2) && (tam <= 6) ){
 		campo.value = vr.substr( 0, tam - 2 ) + '-' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 7) && (tam <= 9) ){
 		campo.value = vr.substr( 0, tam - 6 ) + '/' + vr.substr( tam - 6, 4 ) + '-' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 10) && (tam <= 12) ){
 		campo.value = vr.substr( 0, tam - 9 ) + '.' + vr.substr( tam - 9, 3 ) + '/' + vr.substr( tam - 6, 4 ) + '-' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 13) && (tam <= 14) ){
 		campo.value = vr.substr( 0, tam - 12 ) + '.' + vr.substr( tam - 12, 3 ) + '.' + vr.substr( tam - 9, 3 ) + '/' + vr.substr( tam - 6, 4 ) + '-' + vr.substr( tam - 2, tam ) ; }
 	if ( (tam >= 15) && (tam <= 17) ){
 		campo.value = vr.substr( 0, tam - 14 ) + '.' + vr.substr( tam - 14, 3 ) + '.' + vr.substr( tam - 11, 3 ) + '.' + vr.substr( tam - 8, 3 ) + '.' + vr.substr( tam - 5, 3 ) + '-' + vr.substr( tam - 2, tam ) ;}	
}

function formataCPF(campo){
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length ;
	if ( tam <= 2 ){
 		campo.value = vr ;}
	if ( tam > 2 && tam <= 5 ){
		campo.value = vr.substr( 0, tam - 2 ) + '-' + vr.substr( tam - 2, tam );}
	if ( tam >= 6 && tam <= 8 ){
		campo.value = vr.substr( 0, tam - 5 ) + '.' + vr.substr(tam - 5, 3 ) + '-' + vr.substr( tam - 2, tam );}
	if ( tam >= 9 && tam <= 11 ){
		campo.value = vr.substr( 0, tam - 8 ) + '.' + vr.substr( tam - 8, 3 ) + '.' + vr.substr(tam - 5, 3 ) + '-' + vr.substr( tam - 2, tam );}

}

function formataPercentual(campo) {
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;

	if ( tam <= 3 ){ 
 		campo.value = vr ; }
 	if ( (tam > 3) && (tam <= 6) ){
 		campo.value = vr.substr( 0, tam - 3 ) + ',' + vr.substr( tam - 3, tam ) ; }	
}

function formataTelefone(campo) {
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;

	if ( tam <= 4 )
		campo.value = vr;
	if ( tam > 4 ) 
		campo.value = vr.substr(0, tam-4 ) + '-' + vr.substr(tam-4, tam);
}
function contacampo(campo, tamtxt) {
	document.forms.aapf[tamtxt].value =  1540-document.forms.aapf[campo].value.length;
}

function limita(campo){
	var tamanho = document.forms.aapf[campo].value.length;
	var tex=document.forms.aapf[campo].value;
	if (tamanho>=1539) {
		document.forms.aapf[campo].value=tex.substring(0,1539); 
	}
	return true;
}

function mudaFoco(campofoco)
{
	focaCampo(campofoco);
}

function focaCampo(campofoco) { 
	if(campofoco == '')
		campofoco = 'botaoConfirma.x';
	var num = parseInt(campofoco);
	if ( num || num == 0 )
	{
		if ( document.forms.aapf[num] ) 
		{
			try 
			{
				document.forms.aapf[num].focus();
			} catch (err) {}
		}
	}
	else 
	{
		if ( (campofoco == "senhaConta" || campofoco == "senhaAtual") && document.applets["tclJava"] ) {
			try 
			{
				document.applets["tclJava"].setFocus();
			} catch (err) {}
			
		} else if ( document.forms.aapf[campofoco] )
		{
			var campo = document.forms.aapf[campofoco];
			if(campo.length)
			{
				for (i = 0; i < campo.length; i++) {
					if (campo[i].type == 'submit'){
						try 
						{
							campo[i].focus();
						} catch (err) {}
						break;
					}
				}
			}
			else
			{
				try
				{
					campo.focus();
				} catch (err) {}
			}
		}
	}
}

function formataCartaoCredito(campo, teclapres) {
	
    var tammax = 16;
	var tecla = teclapres.keyCode;
	vr = document.forms.aapf[campo].value;

	if ( tecla == 8 || (tecla >= 48 && tecla <= 57) || (tecla >= 96 && tecla <= 105) ) {
		
		vr = vr.replace( "/", "" );
		vr = vr.replace( "/", "" );
		vr = vr.replace( ",", "" );
		vr = vr.replace( ".", "" );
		vr = vr.replace( ".", "" );
		vr = vr.replace( ".", "" );
		vr = vr.replace( ".", "" );
		vr = vr.replace( "-", "" );
		vr = vr.replace( "-", "" );
		vr = vr.replace( "-", "" );
		vr = vr.replace( "-", "" );
		vr = vr.replace( "-", "" );
		tam = vr.length;

		if (tam < tammax && tecla != 8) {
		   tam = vr.length + 1 ;
		}

		if (tecla == 8 ) {
			tam = tam - 1 ;
		}
		
		if ( tam > 1 ) {
			
	        if ( vr.substr(0,1) != "3" ) {
	        	
				if ( tam < 5 ) {
					document.forms.aapf[campo].value = vr ;
				} else if ( ( tam >  4 ) && ( tam < 9 ) ) {
				   document.forms.aapf[campo].value = vr.substr( 0, 4 ) + '.' + vr.substr( 4, tam-4 ) ;
				} else if ( ( tam >  8 ) && ( tam < 13 ) ) {
				   document.forms.aapf[campo].value = vr.substr( 0, 4 ) + '.' + vr.substr( 4, 4 ) + '.' + vr.substr( 8, tam-4 ) ;
				} else if ( tam > 12 ) {
				   document.forms.aapf[campo].value = vr.substr( 0, 4 ) + '.' + vr.substr( 4, 4 ) + '.' + vr.substr( 8, 4 ) + '.' + vr.substr( 12, tam-4 );
				}
			 	
			} else {
				
				if ( tam < 5 ) {
			   	   document.forms.aapf[campo].value = vr ;
			   	} else if ( ( tam >  4 ) && ( tam < 10 ) ) {
				   document.forms.aapf[campo].value = vr.substr( 0, 4 ) + '.' + vr.substr( 4, tam-4 ) ;
				} else if ( tam >  9 ) {
				   document.forms.aapf[campo].value = vr.substr( 0, 4 ) + '.' + vr.substr( 4, 5 ) + '.' + vr.substr( 9, tam-4 ) ;
				}
			 	
			}
		
		}
		
	}	
	
}

var http_request = null;
function getConexaoHttp()
{
	if(http_request == null)
	{
		if (window.XMLHttpRequest) 
		{ // Mozilla, Safari, ...
		    http_request = new XMLHttpRequest();
		} 
		else if (window.ActiveXObject) 
		{ // IE
		    try 
		    {
		        http_request = new ActiveXObject("Msxml2.XMLHTTP");
		    } 
		    catch (e) 
		    {
		        try 
		        {
		            http_request = new ActiveXObject("Microsoft.XMLHTTP");
		        } catch (e) {}
		    }
		}
	}
	return http_request;

}

function mostraActive(caminhoApplet, versaoApplet, contraste, numCod, local, idTeclado, idDiv, legenda1, legenda2 )
{
	var oDivTcl = document.getElementById(idDiv);
	var codigo = '<object alt="Senha do auto-atendimento" tabindex="70" codebase="' + caminhoApplet + '/teclado/BBTecladoV' + versaoApplet + '.cab#version=1,0,0,3" CLASSID="CLSID:6F03F00A-71B3-4B59-A934-25686DC63B42" name="' + idTeclado + '" id="' + idTeclado + '" width="316" height="80" NOEXTERNALDATA="true"> ';
	codigo += '<param name="id" value="' + idTeclado + '"> ';
	codigo += '<param name="local" value="cartao"> ';
	codigo += '<param name="campoAnterior" value=""> ';
	codigo += '<param name="campoPosterior" value="98"> ';
	codigo += '<param name="colorLabel" value="#000084"> ';
	codigo += '<param name="colorField" value="#FFFFFA"> ';
	codigo += '<param name="colorBorder" value="#BBBBBB"> ';
	codigo += '<param name="colorBackground" value="#FFFFFF"> ';
	if(local == 'cartao')
	{
		codigo += '<param name="tipoLegenda" value="cartao"> ';
	}
	else if(local == 'definido')
	{
		codigo += '<param name="tipoLegenda" value="definido"> ';
		codigo += '<param name="legenda1" value="' + legenda1 + '"> ';
		codigo += '<param name="legenda2" value="' + legenda2 + '"> ';
	}
	codigo += '<param name="colorEmb" value="azul"> ';
	codigo += '<param name="valorContr" value="' + contraste + '"> ';
	codigo += '<param name="numCod" value="' + numCod + '"> ';
	codigo += '</object>';
	oDivTcl.innerHTML = codigo;
	return true;
}

function mostraErro()
{
	var oDivTcl = document.getElementById('tclTecladoContent');
	oDivTcl.innerHTML = '<br>Ocorreu um erro ao carregar o Teclado Virtual.<br>Pressione F5 ou <a href="/aapf/login.jsp?forcaApplet=sim" >acesse o Teclado<br>Virtual em Java</a><br><br>';
	return true;
}

function setAppletArea(oDivTcl)
{
		try {
			oDivTcl.style.width = 316;
			oDivTcl.style.height = 80;
			oDivTcl.style.backgroundColor = "#f7f7f7";
			oDivTcl.innerHTML =  getMensagemErro();
			oDivTcl.style.display = "block";
		} catch (e2) {}
}


function  montaObjetoApplet(caminhoApplet, versaoApplet, contraste, numCod, local, idTeclado, idDiv, legenda1, legenda2, showCamposLogin ) {
	var oDivTcl = document.getElementById(idDiv);
	var codigo = '<applet alt="Senha do auto-atendimento" tabindex="70" code="br/com/bb/aapf/bbteclado/CampoTeclado.class" cabbase="/aapf/ncresp/teclado/BBTeclado' + versaoApplet + '.cab" archive="/aapf/ncresp/teclado/BBTeclado' + versaoApplet + '.jar" codebase="/aapf/ncresp/teclado/" name="' + idTeclado + '" id="' + idTeclado + '" width="316" height="80" namespace="global" mayscript> ';
	codigo += '<param name="id" value="' + idTeclado + '"> ';
	codigo += '<param name="local" value="' + local + '"> ';
	codigo += '<param name="campoAnterior" value=""> ';
	codigo += '<param name="campoPosterior" value="98"> ';
	codigo += '<param name="colorLabel" value="0x000084"> ';
	codigo += '<param name="colorField" value="0xFFFFFA"> ';
	codigo += '<param name="colorBorder" value="0xBBBBBB"> ';
	codigo += '<param name="colorBackground" value="#FFFFFF"> ';
	if(local == "cartao")
	{
		codigo += '<param name="tipoLegenda" value="cartao"> ';
	}
	else if(local == "definido")
	{
		codigo += '<param name="tipoLegenda" value="definido"> ';
		codigo += '<param name="legenda1" value="' + legenda1 + '"> ';
		codigo += '<param name="legenda2" value="' + legenda2 + '"> ';
	}
	if(showCamposLogin == "true")
	{
		codigo += '<param name="showCamposLogin" value="true"> ';
	}
	codigo += '<param name="colorEmb" value="azul"> ';
	codigo += '<param name="valorContr" value="' + contraste + '"> ';
	codigo += '<param name="numCod" value="' + numCod + '"> ';
	codigo += '</applet>';
	mensagemErro = codigo;
	try {
		if (window.navigator.javaEnabled()&&oDivTcl) {
			var oAppletTcl = document.createElement("applet");

			// oAppletTcl.code = "br.com.bb.aapf.bbteclado.CampoTeclado.class";
			oAppletTcl.code = "br/com/bb/aapf/bbteclado/CampoTeclado.class";
			
			oAppletTcl.codeBase = caminhoApplet + "/teclado/";
			oAppletTcl.cabBase = caminhoApplet + "/teclado/BBTeclado" + versaoApplet + ".cab";
			oAppletTcl.archive = caminhoApplet + "/teclado/BBTeclado" + versaoApplet + ".jar";
			oAppletTcl.name = idTeclado;
			oAppletTcl.tabIndex = "70";
			oAppletTcl.title = "Senha do auto-atendimento";
			oAppletTcl.alt = "Senha do auto-atendimento";
			oAppletTcl.left = 0;
			oAppletTcl.top = 0;
			oAppletTcl.width = 316;
			oAppletTcl.height = 80;
			oAppletTcl.id = idTeclado;
			oAppletTcl.appendChild(makeParam("MAYSCRIPT", "true"));
			oAppletTcl.appendChild(makeParam("id", idTeclado));
			oAppletTcl.appendChild(makeParam("local", local));
			oAppletTcl.appendChild(makeParam("campoAnterior",""));
			oAppletTcl.appendChild(makeParam("campoPosterior","98"));
			oAppletTcl.appendChild(makeParam("colorLabel","0x000084"));
			oAppletTcl.appendChild(makeParam("colorField","0xFFFFFA"));
			oAppletTcl.appendChild(makeParam("colorBorder","0xBBBBBB"));
			oAppletTcl.appendChild(makeParam("colorBackground","#FFFFFF"));
			if(local == "cartao")
			{
				oAppletTcl.appendChild(makeParam("tipoLegenda","cartao"));
			}
			else if(local == "definido")
			{
				oAppletTcl.appendChild(makeParam("tipoLegenda","definido"));
				oAppletTcl.appendChild(makeParam("legenda1",legenda1));
				oAppletTcl.appendChild(makeParam("legenda2",legenda2));
			}
			if(showCamposLogin == "true")
			{
				oAppletTcl.appendChild(makeParam("showCamposLogin","true"));
			}
			oAppletTcl.appendChild(makeParam("colorEmb","azul"));
			oAppletTcl.appendChild(makeParam("valorContr",contraste));
			oAppletTcl.appendChild(makeParam("numCod",numCod));

			oDivTcl.appendChild(oAppletTcl);

		} else {
			window.defaultStatus = "Erro: Java nao suportado ou nao habilitado!";
			window.status = "Erro: Java nao suportado ou nao habilitado!";
			setAppletArea(oDivTcl);
		}
	} catch (e) {
		window.defaultStatus = "Erro ao criar Applet:" + e.description;
		window.status = "Erro ao criar Applet:" + e.description;
		setAppletArea(oDivTcl);
	}

	return true;
}

function makeParam(name, value)
{
	var p = document.createElement("param");
	p.name = name;
	p.value = value;
	return p;
}

function mostraApplet(caminhoApplet, versaoApplet, contraste, numCod, local, idTeclado, idDiv, legenda1, legenda2, showCamposLogin )
{
	var codigo = '<applet alt="Senha do auto-atendimento" tabindex="70" code="br/com/bb/aapf/bbteclado/CampoTeclado.class" cabbase="/aapf/ncresp/teclado/BBTeclado' + versaoApplet + '.cab" archive="/aapf/ncresp/teclado/BBTeclado' + versaoApplet + '.jar" codebase="/aapf/ncresp/teclado/" name="' + idTeclado + '" id="' + idTeclado + '" width="316" height="80" namespace="global" mayscript> ';
	codigo += '<param name="id" value="' + idTeclado + '"> ';
	codigo += '<param name="local" value="' + local + '"> ';
	codigo += '<param name="campoAnterior" value=""> ';
	codigo += '<param name="campoPosterior" value="98"> ';
	codigo += '<param name="colorLabel" value="0x000084"> ';
	codigo += '<param name="colorField" value="0xFFFFFA"> ';
	codigo += '<param name="colorBorder" value="0xBBBBBB"> ';
	codigo += '<param name="colorBackground" value="#FFFFFF"> ';
	if(local == "cartao")
	{
		codigo += '<param name="tipoLegenda" value="cartao"> ';
	}
	else if(local == "definido")
	{
		codigo += '<param name="tipoLegenda" value="definido"> ';
		codigo += '<param name="legenda1" value="' + legenda1 + '"> ';
		codigo += '<param name="legenda2" value="' + legenda2 + '"> ';
	}
	if(showCamposLogin == "true")
	{
		codigo += '<param name="showCamposLogin" value="true"> ';
	}
	codigo += '<param name="colorEmb" value="azul"> ';
	codigo += '<param name="valorContr" value="' + contraste + '"> ';
	codigo += '<param name="numCod" value="' + numCod + '"> ';
	codigo += '</applet>';
	oDivTcl.innerHTML = codigo;
	return true;
}

function mostraAssinador(width, caminhoApplet, nomeBotaoSubmit, idCartao, parametroD, versao)
{
	var oDivTcl = document.getElementById("tclTecladoContent");
	var codigo = '<applet name="tclAssinador" id="tclAssinador" code="br.com.bb.cdg.assinador.applet.SignApplet" codebase="' + caminhoApplet + '/certificacao/" archive="' + caminhoApplet + '/certificacao/slogin' + versao + '.jar" width="' + width + '" height="60" mayscript="mayscript" alt="Assinador do auto-atendimento"> ';
	codigo += '<param name="botao" value="' + nomeBotaoSubmit + '"> ';
	if(idCartao != "null")
		codigo += '<param name="c" value="' + idCartao + '"> ';
	codigo += parametroD + ' ';
	codigo += 'Seu assinador do auto-atendimento não foi habilitado.<br> ';
	codigo += '<a href="/aapf/ajuda/faqCertificacao.jsp">Clique aqui para saber mais...</a><br><br> ';
	codigo += '</applet>';
	oDivTcl.innerHTML = codigo;
	return true;
}

// Função utilizada pela COCI (Minhas mensagens e BB Responde)
function mostraAjuda(objeto){
	document.getElementById(objeto).style.visibility = "visible";
}

// Função utilizada pela COCI (Minhas mensagens e BB Responde)
function escondeAjuda(objeto){
	document.getElementById(objeto).style.visibility = "hidden";
}

// Função utilizada pela 852/952
function mostraDiv(objeto){
	document.getElementById(objeto).style.visibility = "visible";
}

// Função utilizada pela 852/952
function escondeDiv(objeto){
	document.getElementById(objeto).style.visibility = "hidden";
}

// Função Quebra Página na impressão. Utilizado no Consórcio
function quebraPagina() {
var ns4 = (document.layers)? true:false;
var moz = (window.navigator.appCodeName);
var ns6 = (document.getElementById)? true:false;
var ie4 = (document.all)? true:false;
var ie5 = false;

if (ie4) {
	if ((navigator.userAgent.indexOf('MSIE 5') > 0) || (navigator.userAgent.indexOf('MSIE 6') > 0)) {
		ie5 = true;
	}
	if (ns6) {
		ns6 = false;
	}
}
	if (ie4)
	document.write ("<p class='quebra'></p>");
	else if (ns4)
	document.write ("");
	else if (moz == "Mozilla")
	document.write ("<a class='quebra'>&nbsp;</a>");
	else
	document.write ("Esse navegador não suporta impressão com quebra de páginas");
}

// Controle de Abertura e Fechamento dos Boxes
function abrirDiv(idDiv){ 
	divStyle_ = document.getElementById(idDiv).style;
	btaStyle_ = document.getElementById(idDiv + '_a').style;
	btfStyle_ = document.getElementById(idDiv + '_f').style;
	divStyle_.display = ''; 
	btaStyle_.display = 'none'; 
	btfStyle_.display = ''; 
	return false; 
}

function fecharDiv(idDiv){
	divStyle_ = document.getElementById(idDiv).style;
	btaStyle_ = document.getElementById(idDiv + '_a').style;
	btfStyle_ = document.getElementById(idDiv + '_f').style;
	divStyle_.display = 'none'; 
	btaStyle_.display = ''; 
	btfStyle_.display = 'none'; 
	return false; 
}

function insDigito(digito)
{
	if( document.getElementById('senhaConta_').value.length < 8 ) 
		document.getElementById('senhaConta_').value += digito;
}

function delDigito()
{
	var str = document.getElementById('senhaConta_').value;
	document.getElementById('senhaConta_').value = str.substring( 0, str.length - 1 );
}

function setAlpha( target, acaoContraste ){
	var alpha = "";
	var vlrCookie = getCookie( "aapf.teclado.contraste" );
	var contraste = 3;
	
	if( vlrCookie != null && vlrCookie != "" )
		contraste = parseInt( vlrCookie );
	else
		contraste = parseInt( document.getElementById('valorContr').value );

	if( contraste > 5 )
		contraste = 5;
	
	if( acaoContraste != null && acaoContraste != "" )
	{
		if( acaoContraste == "mais" )
		{	
			if( contraste < 5 )
				contraste = parseInt(contraste) + 1;
		}		
		else if( acaoContraste == "menos" )
		{
			if( contraste != 0 && contraste > 1 )
				contraste = parseInt(contraste) - 1; 
		}
	}

	if( contraste == 1 )
		alpha = 10;
	else if( contraste == 2 )
		alpha = 25;
	else if( contraste == 3 )
		alpha = 40;
	else if( contraste == 4 )
		alpha = 65;
	else if( contraste == 5 )
		alpha = 100;
	
	target = document.getElementById( target );
	target.style.filter	 = "alpha(opacity="+ alpha +")";
	target.style.opacity = alpha/100;
	
	// Guarda valores atualizados.
	setCookie( "aapf.teclado.contraste", contraste, 1 );
	document.getElementById('valorContr').value = contraste;  
}

function focaCampoVazioLogin() {
	try {
		if (document.getElementsByName("dependenciaOrigem")[0].value == "") {focaCampo('dependenciaOrigem');return;}
		else if (document.getElementsByName("numeroContratoOrigem")[0].value  == "") {focaCampo('numeroContratoOrigem');return;}
		else if (document.getElementsByName("senhaConta")[0].value == ""){focaCampo('senhaConta');return;}
	} catch (err) {
		// Não correntista
		try {
			if (document.getElementsByName("cpf")[0].value == "") {focaCampo('cpf');return;}
			else if (document.getElementsByName("senhaConta")[0].value == ""){focaCampo('senhaConta');return;}
		} catch (err) {}
	} 
}

// Permite a marcação do checkbox e alteração da cor da respectiva linha, ao
// clicar em qualquer área da "TR", além de
// controlar o número máximo de checkbox selecionados.
// Utiliza a variável global 'statusInicial', que é alterada na função
// "mudaCor".
function marcaCheckbox( valor, qtdeMax )
{
	var inputs = document.getElementsByTagName('input');
	var contChecked = 0;
	
	for( var x = 0; x < inputs.length; x++ ){
		if( inputs[x].type == "checkbox" && inputs[x].value == valor ){
			inputs[x].checked = statusInicial == true ? false : true;
			document.getElementById( valor ).style.backgroundColor = inputs[x].checked == true ? "#bebebe" : "";
			statusInicial = inputs[x].checked; 
		}
	
		if( inputs[x].checked == true )		
			contChecked += 1;
		
		if( contChecked > qtdeMax ){
			inputs[x].checked = false;
			document.getElementById( valor ).style.backgroundColor = "";
		}
	}
}

// Altera cor da "TR".
function mudaCor( valor, acao )
{
	var inputs = document.getElementsByTagName('input');
	
	for( var x = 0; x < inputs.length; x++ ){	
		if( inputs[x].type == "checkbox" && inputs[x].value == valor  ){	
			statusInicial = inputs[x].checked; 	
			if( inputs[x].checked == false ){	
				if( acao == "mudacor" )
					document.getElementById( valor ).style.backgroundColor = "#bebebe";
				else if( acao == "remove" )
					document.getElementById( valor ).style.backgroundColor = "";
			}
		}	
	}	
}

function desmarcaTodos( nome )
{
	var inputs = document.getElementsByTagName('input');
	
	for( var x = 0; x < inputs.length; x++ ){	
		if( inputs[x].type == "checkbox" && inputs[x].name == nome ){
			inputs[x].checked = false;
			document.getElementById( inputs[x].value ).style.backgroundColor = "";
		}
	}
}

function formataNumerico(campo) {
    var result = "";
    var vr = filtraCampo(campo);
    var tam = vr.length;
    var validChars = "0123456789";
    for (var n = 0; n < tam; n++) {
        if (validChars.indexOf(vr.substring(n, n+1)) != -1)
            result += vr.substring(n, n+1);
    }
    campo.value = result;
    return result;
}

function formataDado(campo, tammax, pos, teclapres) {
	var keyCode;
	if (teclapres.srcElement) {
		keyCode = teclapres.keyCode; 
	} else if (teclapres.target) {
		keyCode = teclapres.which; 
	}
	if (keyCode == 0 || keyCode == 8) {
		return true;
	}
	if ((keyCode < 48 || keyCode > 57) && keyCode != 88 && (keyCode != 120)) {
		return false;
	}
	var tecla = keyCode;
	vr = campo.value;
	vr = vr.replace("-", "");
	vr = vr.replace("/", "");
	tam = vr.length;
	if (tam < tammax && tecla != 8) {
		tam = vr.length + 1;
	}
	if (tecla == 8) {
		tam = tam - 1;
	}
	if (tecla == 8 || tecla == 88 || tecla >= 48 && tecla <= 57 || tecla >= 96 && tecla <= 105 || tecla == 120) {
		if (tam <= 2) {
			campo.value = vr;
		}
		if (tam > pos && tam <= tammax) {
			campo.value = vr.substr(0, tam - pos) + "-" + vr.substr(tam - pos, tam);
		}
	}
}
