$(window).load(function() {
	tamanho();
});

$(window).resize(function() {
	tamanho();
});

function tamanho() {
	var x = document.body.scrollWidth || document.body.offsetWidth;
	var y = document.body.scrollHeight || document.body.offsetHeight;
	redimensionarGrid([ {
		name : 'tamanho',
		value : y.toString()
	} ]);
}