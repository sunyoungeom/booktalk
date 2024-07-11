let currentAuthor = document.getElementById('username').value;
let currentTitle = '';
let currentPage = 0;
const pageSize = 5;

function fetchReviews(page, title = '', author = '', sortBy = '') {
    const table = document.querySelector('.table');
    const contentColumns = table.querySelectorAll('.content-column');

    let url = `/api/reviews?author=${author}&&page=${page}&size=${pageSize}`;

    const contentColumnNumber = document.getElementById('content-column-number');
    const contentColumnTitle = document.getElementById('content-column-title');
    const contentColumnDate = document.getElementById('content-column-date');
    const contentColumnAction = document.getElementById('content-column-action');

    fetch(url)
        .then(response => {
            if (!response.ok) {
                if (response.status === 404) {
                    return response.json().then(json => {
                        const errorMessage = json.message;
                        console.error(errorMessage);
                        clearContents();
                        clearPagination();
                        displayErrorMessage(errorMessage);
                        throw new Error(errorMessage);
                    });
                }
                throw new Error('서버에서 오류 응답을 받았습니다.');
            }
            return response.json();
        })
        .then((json) => {
            clearContents();
            const reviews = json.data.content;
            const totalElements = json.data.totalElements;

            reviews.forEach((review, index) => {
                // 글번호
                const numberDiv = document.createElement('div');
                numberDiv.className = 'head head-1 head-4';
                numberDiv.innerHTML = `<div class="frame-521"><div class="text presetsbody2">${totalElements - (pageSize * page) - index}</div></div>`;
                contentColumnNumber.appendChild(numberDiv);

                // 책 제목
                const titleDiv = document.createElement('div');
                titleDiv.className = 'head head-1 head-4';
                titleDiv.innerHTML = `<div class="text-2 manrope-regular-normal-mirage-16px">${review.title}</div>`;
                contentColumnTitle.appendChild(titleDiv);

                // 작성일
                const dateDiv = document.createElement('div');
                dateDiv.className = 'head head-1 head-4';
                dateDiv.innerHTML = `<div class="text presetsbody2">${review.date}</div>`;
                contentColumnDate.appendChild(dateDiv);

                // 수정 버튼
                const actionDiv = document.createElement('div');
                actionDiv.className = 'head-2 head';
                actionDiv.innerHTML = `<div class="light-button" style="cursor: pointer;" onclick="editFunction(${review.id})">
                                        <div class="text-3 valign-text-middle presetsbody2">수정</div></div>`;
                contentColumnAction.appendChild(actionDiv);

            });
            setupPagination(totalElements, page);
        });

}

function clearContents() {
    const contentColumnNumber = document.getElementById('content-column-number');
    const contentColumnTitle = document.getElementById('content-column-title');
    const contentColumnDate = document.getElementById('content-column-date');
    const contentColumnAction = document.getElementById('content-column-action');
    contentColumnNumber.innerHTML = '';
    contentColumnTitle.innerHTML = '';
    contentColumnDate.innerHTML = '';
    contentColumnAction.innerHTML = '';
}

function displayErrorMessage(message) {
    const container = document.getElementById('errorContainer');
    container.innerHTML = `<div class="inter-normal-black-20px" style="margin-top:100px; display: flex;
    justify-content: center;">${message}</div>`;
    container.style.display = 'block';
}

function editFunction(id) {
    window.location.href = `/reviews/edit/${id}`;
}

document.addEventListener("DOMContentLoaded", function () {
    // 작성한 리뷰 로드
    fetchReviews(currentPage, '', currentAuthor, '');
});
