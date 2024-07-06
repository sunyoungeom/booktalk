let currentAuthor = '';
let currentTitle = '';
let currentPage = 0;
const pageSize = 5;
const userId = document.getElementById('userId').value;

function fetchReviews(page, title = '', author = '', sortBy = '') {
    const table = document.querySelector('.table');
    const contentColumns = table.querySelectorAll('.content-column');

    let url = `/api/reviews/${userId}?page=${page}&size=${pageSize}`;

    const contentColumnNumber = document.getElementById('content-column-number');
    const contentColumnTitle = document.getElementById('content-column-title');
    const contentColumnDate = document.getElementById('content-column-date');
    const contentColumnAuthor = document.getElementById('content-column-author');

    fetch(url)
        .then(response => {
            if (!response.ok) {
                if (response.status === 404) {
                    return response.json().then(json => {
                        const errorMessage = json.message;
                        console.error(errorMessage);
                        clearContents();
                        displayErrorMessage(errorMessage);
                        throw new Error(errorMessage);
                    });
                }
                throw new Error('서버에서 오류 응답을 받았습니다.');
            }
            return response.json();
        })
        .then((json) => {
            console.log(json)
            clearContents();
            if (!json.reviews || json.reviews.length === 0) {
                throw new Error('리뷰 검색결과가 없습니다.');
            }
            const reviews = json.reviews.content;
            const totalElements = json.reviews.totalElements;

            reviews.forEach((review, index) => {
                // 글번호
                const numberDiv = document.createElement('div');
                numberDiv.className = 'head head-1 head-4';
                numberDiv.innerHTML = `<div class="frame-521"><div class="text presetsbody2">${totalElements - (pageSize * page) - index}</div></div>`;
                contentColumnNumber.appendChild(numberDiv);

                // 책 제목
                const titleDiv = document.createElement('div');
                titleDiv.className = 'head head-1 head-4';
                titleDiv.innerHTML = `<div class="text-2 manrope-regular-normal-mirage-16px" style="cursor:pointer;"
                                        onclick="window.location.href='/reviews/search?title=' + encodeURIComponent('${review.title}') 
                                        + '&author=' + encodeURIComponent('${review.author}')">${review.title}</div>`;
                contentColumnTitle.appendChild(titleDiv);

                // 작성일
                const dateDiv = document.createElement('div');
                dateDiv.className = 'head head-1 head-4';
                dateDiv.innerHTML = `<div class="text presetsbody2">${review.date}</div>`;
                contentColumnDate.appendChild(dateDiv);

                // 작성자
                const authorDiv = document.createElement('div');
                authorDiv.className = 'head head-1 head-4';
                authorDiv.innerHTML = `<div class="text presetsbody2">${review.author}</div>`;
                contentColumnAuthor.appendChild(authorDiv);

            });
            setupPagination(totalElements, page);
        });

}

function clearContents() {
    const contentColumnNumber = document.getElementById('content-column-number');
    const contentColumnTitle = document.getElementById('content-column-title');
    const contentColumnDate = document.getElementById('content-column-date');
    const contentColumnAuthor = document.getElementById('content-column-author');
    contentColumnNumber.innerHTML = '';
    contentColumnTitle.innerHTML = '';
    contentColumnDate.innerHTML = '';
    contentColumnAuthor.innerHTML = '';
}

function displayErrorMessage(message) {
    const container = document.getElementById('errorContainer');
    container.innerHTML = `<div class="inter-normal-black-20px" style="margin-top:100px; display: flex;
    justify-content: center;">${message}</div>`;
    container.style.display = 'block';
}

document.addEventListener("DOMContentLoaded", function () {
    // 좋아요한 리뷰 로드
    fetchReviews(currentPage, '', '', '');
});
