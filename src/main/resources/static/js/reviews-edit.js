var previousContent = document.getElementById('previousContent').value;
document.getElementById("reviewContent").value = previousContent;

// 리뷰 수정
function submitForm(event) {
    event.preventDefault();

    var reviewId = document.getElementById('reviewId').value;
    var reviewContent = document.getElementById("reviewContent").value;

    if (previousContent === reviewContent) {
        alert("리뷰 내용을 수정하세요.");
        return;
    }

    var data = {
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
            if (response.ok) {
                console.log(data)
                alert("리뷰가 수정되었습니다.");
                window.location.href = '/reviews/list';
            } else {
                alert('리뷰 수정에 실패하였습니다.')
                console.error('리뷰 수정에 실패하였습니다.');
            }
        })
        .catch(error => {
            console.error(error);
        });
}

function previousPage() {
    history.back();
}