var reviewId = document.getElementById('reviewId').value;
var sessionId = document.getElementById('session-userId').value;

function fetchReviews(reviewId) {
    fetch('/api/reviews/' + reviewId, {
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버에서 오류 응답을 받았습니다.');
            }
            return response.json();
        })
        .then((data) => {
            const review = data.data;
            const reviewAuthor = document.getElementById('reviewAuthor');
            const reviewCreationDate = document.getElementById('reviewCreationDate');
            const reviewTitle = document.getElementById('reviewTitle');
            const previousContent = document.getElementById('previousContent');
            const reviewContent = document.getElementById('reviewContent');

            reviewAuthor.textContent = review.author;
            reviewCreationDate.textContent = review.date;
            reviewTitle.textContent = review.title;
            previousContent.value = review.content;
            reviewContent.textContent = review.content;
        })
        .catch(error => {
            console.error(error);
        });
}

// 리뷰 수정
function submitForm(event) {
    event.preventDefault();

    var reviewId = document.getElementById('reviewId').value;
    const previousContent = document.getElementById('previousContent').value;

    var reviewContent = document.getElementById("reviewContent").value;

    if (previousContent === reviewContent) {
        alert("리뷰 내용을 수정하세요.");
        return;
    }

    var data = {
        "userId": sessionId,
        "content": reviewContent,
    };

    fetch('/api/reviews/' + reviewId, {
        method: 'PUT',
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
            alert(data.message);
            window.location.href = '/reviews/list';
        })
        .catch(error => {
            console.error(error);
        });
}

function previousPage() {
    history.back();
}

document.addEventListener("DOMContentLoaded", function () {
    fetchReviews(reviewId);
});