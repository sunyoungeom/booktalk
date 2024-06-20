document.addEventListener('DOMContentLoaded', function () {
    var inputField = document.querySelector('.x04-search-input input');

    inputField.addEventListener('keypress', function (event) {
        var inputValue = inputField.value;
        if (!/^[a-zA-Z0-9가-힣\s]*$/.test(inputValue + event.key)) {
            event.preventDefault();
        }
    });

    inputField.addEventListener('input', function (event) {
        var inputValue = inputField.value;
        inputField.value = inputValue.replace(/[^\w\sㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
    });


    inputField.addEventListener('keypress', function (event) {
        if (event.key === 'Enter') {
            var inputValue = inputField.value;
            if (inputValue.trim() !== '') {
                window.location.href = '/books/search/' + inputValue + '/1';
            }
        }
    });
});