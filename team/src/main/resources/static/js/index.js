function shortURL() {
    let originalURL = document.myForm.elements[0].value;
    let customURL = document.myForm.elements[1].value;
    let createQR = document.myForm.elements[2].checked;
    let checkSafe = document.myForm.elements[3].checked;

    let obj = new Object();
    obj.originaluRL = originalURL;
    if(customURL == ""){
        obj.customURL = null;
    }
    else{
        obj.customURL = customURL;
    }
    obj.createQr = createQR;
    obj.checkSafe = checkSafe;
    let jsonString = JSON.stringify(obj);

    let xhr = new XMLHttpRequest();
    let url = "localhost:8080/link";
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var json = JSON.parse(xhr.responseText);
            document.getElementById("message").innerHTML = json;
        }
    };
    xhr.send(jsonString);





}