function generateQRCode(type) {
    console.log("Tipo de pagamento selecionado:", type);
    if (type === 'credit_card') {
        console.log("Gerar QR Code para Cartão de Crédito...");
        // Aqui você pode adicionar o código para gerar o QR Code para o cartão de crédito
    }
}

function generatePixQRCode() {
    console.log("Gerar QR Code para PIX...");
    document.getElementById("qrcode").src = "qrcode_pix.png"; // Substitua com o nome da imagem do QR Code para PIX
    document.getElementById("qrcode").style.display = "block";

    setTimeout(function() {
        document.getElementById("qrcode").style.display = "none";
    }, 30000); // 30 segundos
}
