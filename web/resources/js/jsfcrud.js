/* global PF, limite_minimo */

function handleSubmit(args, dialog) {
    var jqDialog = jQuery('#' + dialog);
    if (args.validationFailed) {
        jqDialog.effect('shake', {times: 3}, 100);
    } else {
        PF(dialog).hide();
    }
}

function numero(e)
{
    var keynum = window.event ? window.event.keyCode : e.which;
    if ((keynum === 8) || (keynum === 13) || (keynum === 6))
        return true;
    return /\d/.test(String.fromCharCode(keynum));

}


function letra(l) {
    var key = window.event ? window.event.keyCode : l.which;
    tecla = String.fromCharCode(key).toString();
    letras = " Ã¡Ã©Ã­Ã³ÃºabcdefghijklmnÃ±opqrstuvwxyzÃ�Ã‰Ã�Ã“ÃšABCDEFGHIJKLMNÃ‘OPQRSTUVWXYZ";//Se define todo el abecedario que se quiere que se muestre.
    especiales = [8, 37, 39, 46, 6]; //Es la validaciÃ³n del KeyCodes, que teclas recibe el campo de texto.
    tecla_especial = false;
    for (var i in especiales) {
        if (key === especiales[i]) {
            tecla_especial = true;
            break;
        }
        if (letras.indexOf(tecla) === -1 && !tecla_especial) {
            return false;
        }
    }
}

function minimo(){
    var min = document.getElementById("telefono");
    if(min.value.length > 6 && min.value.length < 12){
        return true;
    }
}