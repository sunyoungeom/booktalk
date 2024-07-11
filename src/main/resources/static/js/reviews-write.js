const userId = document.getElementById('session-userId').value;
const username = document.getElementById('session-username').value;

// 오늘 날짜
function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}

document.addEventListener("DOMContentLoaded", function () {
    var currentDate = formatDate(new Date());
    document.getElementById("currentDate").textContent = currentDate;
});

// 리뷰 등록
function submitForm(event) {
    event.preventDefault();

    var title = document.getElementById('currentTitle').value;
    var userId = document.getElementById('userId').value;
    var author = document.getElementById('username').value;
    var currentDate = formatDate(new Date());
    var reviewContent = document.getElementById("reviewContent").value;

    if (!reviewContent) {
        alert("리뷰 내용을 입력하세요.");
        return;
    }

    var data = {
        "userId": userId,
        "title": title,
        "author": author,
        "content": reviewContent,
    };

    fetch('/api/reviews', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(json => {
                    const errorMessage = json.data[0];
                    alert(errorMessage)
                    throw new Error(errorMessage);
                });
            }
            return response.json();
        })
        .then(data => {
            console.log(data)
            alert("리뷰가 등록되었습니다.");
            window.location.href = '/reviews/search?title=' + title;
        })
        .catch(error => {
            console.error(error);
        });
}