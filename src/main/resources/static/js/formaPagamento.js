function generateQRCode(type) {
    console.log("Tipo de pagamento selecionado:", type);
    if (type === 'credit_card') {
        console.log("Gerar QR Code para Cartão de Crédito...");
    }
}

function generatePixQRCode() {
    console.log("Gerar QR Code para PIX...");
    document.getElementById("qrcode").src = "qrcode_pix.png"; /
    document.getElementById("qrcode").style.display = "block";

    setTimeout(function() {
        document.getElementById("qrcode").style.display = "none";
    }, 10000);
}
