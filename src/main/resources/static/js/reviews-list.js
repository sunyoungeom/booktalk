let currentTitle = document.getElementById('currentTitle').value;

function fetchAndRenderReviews(title = '', author = '', sortBy = '') {
    let url = '/api/reviews?';
    const params = new URLSearchParams();

    if (title) params.append('title', title);
    if (author) params.append('author', author);
    if (sortBy) params.append('sortBy', sortBy);

    url += params.toString();

    fetch(url)
        .then(response => {
            if (!response.ok) {
                if (response.status === 404) {
                    return response.json().then(json => {
                        const errorMessage = json.message;
                        console.error(errorMessage);
                        clearContentsAddSpinner();

                        setTimeout(() => {
                            removeSpinner();
                            displayErrorMessage(errorMessage);
                        }, 500);

                        throw new Error(errorMessage);
                    });
                }
                throw new Error('서버에서 오류 응답을 받았습니다.');
            }
            return response.json();
        })
        .then((json) => {
            clearContentsAddSpinner();
            if (!json.reviews || json.reviews.length === 0) {
                throw new Error('리뷰 검색결과가 없습니다.');
            }

            const reviews = json.reviews;
            const container = document.getElementById('feed');

            setTimeout(() => {
                removeSpinner();
                reviews.forEach(review => {
                    container.innerHTML += createReviewHTML(review);
                });
            }, 500);
        })
        .catch(error => {
            console.error(error);
        });
}

// 컨텐츠 초기화 & 스피너 추가
function clearContentsAddSpinner() {
    const reviewContainer = document.getElementById('feed');
    const errorContainer = document.getElementById('errorContainer');
    reviewContainer.innerHTML = '';
    errorContainer.innerHTML = '';
    addSpinner();

}

// 스피너 추가
function addSpinner() {
    const spinner = document.getElementById('spinner');
    spinner.style.display = 'block';
}

// 스피너 제거
function removeSpinner() {
    const spinner = document.getElementById('spinner');
    spinner.style.display = 'none';
}

// 오류 메시지 출력
function displayErrorMessage(message) {
    const container = document.getElementById('errorContainer');

    container.innerHTML = `
        <div class="inter-normal-black-20px" style="margin-top:100px">
            ${message}
        </div>
    `;

    setTimeout(() => {
        container.style.display = 'block';
    }, 500);

}

// 리뷰 생성
function createReviewHTML(review) {
    return `
                <div class="review">
                    <div class="top-5">
                        <img class="image rectangle-1" src="/img/rectangle-1.png" alt="Image" />
                        <div class="container">
                            <div class="name-5">
                                <input type="hidden" id="currentReview" th:value="${review.id}">
                                <div class="book-title first-last manrope-semi-bold-mirage-16px">${review.title}</div>
                                <div class="username manrope-normal-storm-gray-14px">${review.author}</div>
                            </div>
                        </div>
                    </div>
                    <div class="container-1">
                        <div class="paragraph-5">
                            <p class="text-3 manrope-normal-mirage-16px">${review.content}</p>
                            <div class="dropdown">
                                <div class="text-4 manrope-semi-bold-mirage-14px">더보기</div>
                                <img class="icons-6" src="/img/icons.svg" alt="Icons" />
                            </div>
                        </div>
                        <div class="date manrope-normal-storm-gray-12px">${review.date}</div>
                    </div>
                    <div class="icons">
                        <div class="frame-2610462">
                            <img class="heart-5" src="/img/heart-3.svg" alt="heart" onclick="likeFunction(${review.id})"/>
                            <div class="address-5 small-text">${review.likes} 좋아요</div>
                        </div>
                    </div>
                    <div class="divider"></div>
                </div>
            `;
}

function likeFunction(id) {
    console.log(id)
}




document.addEventListener("DOMContentLoaded", () => {
    // 검색한 제목으로 리뷰 로드
    fetchAndRenderReviews(currentTitle, '', '');

    // '인기순' 리뷰 로드
    const popularityDiv = document.getElementById('popularity');
    popularityDiv.addEventListener('click', () => fetchAndRenderReviews(currentTitle, '', 'popularity'));

    // '최신순' 리뷰 로드
    const latestDiv = document.getElementById('latest');
    latestDiv.addEventListener('click', () => fetchAndRenderReviews(currentTitle, '', ''));

    // 검색 입력 필드
    const inputField = document.querySelector('.x04-search-input input');

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
                console.log(inputValue);
                currentTitle = inputValue;

                fetchAndRenderReviews(currentTitle, '', '');
            }
        }
    });
});