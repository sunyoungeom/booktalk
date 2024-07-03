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

    const profileImg = document.getElementById('profileImgPath');
    profileImg.src = sessionStorage.getItem('profileImgPath');
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
        "date": currentDate,
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
            if (response.ok) {
                console.log(data)
                alert("리뷰가 등록되었습니다.");
                window.location.href = '/reviews/search?title=' + title;
            } else {
                alert('리뷰 등록에 실패하였습니다.')
                console.error('리뷰 등록에 실패하였습니다.');
            }
        })
        .catch(error => {
            console.error(error);
        });
}