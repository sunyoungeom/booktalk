const reviewsContainer = document.getElementById('feed');

let currentAuthor = document.getElementById('currentAuthor').value;
let currentTitle = document.getElementById('currentTitle').value;
let currentPage = 0;
const pageSize = 5;

function fetchReviews(page, title = '', author = '', sortBy = '') {
    let url = `/api/reviews?page=${page}&size=${pageSize}`;
    const params = new URLSearchParams();

    if (title) params.append('title', title);
    if (author) params.append('author', author);
    if (sortBy) params.append('sortBy', sortBy);

    url += '&' + params.toString();

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
            clearContents();
            if (!json.reviews || json.reviews.length === 0) {
                throw new Error('리뷰 검색결과가 없습니다.');
            }

            const reviews = json.reviews.content;
            const totalElements = json.reviews.totalElements;
            reviewsContainer.innerHTML = ''; // 리뷰 목록 초기화
            currentTitle = '' // 작성자 검색 초기화

            reviews.forEach(review => {
                reviewsContainer.innerHTML += createReviewHTML(review);
                if (review.liked) {
                    const heartIcon = document.getElementById(`heartIcon${review.id}`);
                    heartIcon.src = "/img/heart-liked.svg";
                }
            });
            setupPagination(totalElements, page);
        })
        .catch(error => {
            console.error(error);
        });
}

function clearContents() {
    const errorContainer = document.getElementById('errorContainer');
    reviewsContainer.innerHTML = '';
    errorContainer.innerHTML = '';
}

function displayErrorMessage(message) {
    const container = document.getElementById('errorContainer');
    container.innerHTML = `<div class="inter-normal-black-20px" style="margin-top:100px">${message}</div>`;
    container.style.display = 'block';
}

function createReviewHTML(review) {
    let editAndDeleteButtons = '';
    const userId = document.getElementById('userId').value;
    if (userId == review.userId) {
        editAndDeleteButtons = `
                <div class="head">
                    <div class="light-button-3 light-button-5" style="cursor: pointer;" onclick="editFunction(${review.id})">
                        <div class="text-7 valign-text-middle manrope-normal-storm-gray-16px">수정</div>
                    </div>
                </div>
                <div class="head">
                    <div class="light-button-4 light-button-5" style="cursor: pointer;" onclick="deleteFunction(${review.id})">
                        <div class="text-8 valign-text-middle presetsbody3">삭제</div>
                    </div>
                </div>
            `;
    }

    return `
            <div class="review">
                <div class="top-5">
                    <img class="image rectangle-1" src="${review.user.profileImgPath}" alt="Image" />
                    <div class="container">
                        <div class="name-5">
                            <input type="hidden" id="currentReview" value="${review.id}">
                            <div class="book-title first-last manrope-semi-bold-mirage-16px" style="cursor:pointer;"
                            onclick="window.location.href='/books/detail/' + encodeURIComponent('${review.title}')">${review.title}
                            </div>
                            <div class="username manrope-normal-storm-gray-14px">${review.author}</div>
                        </div>
                    </div>
                </div>
                <div class="container-1">
                    <div class="paragraph-5">
                        <p class="text-3 manrope-normal-mirage-16px">${review.content}</p>
                    </div>
                    <div class="date manrope-normal-storm-gray-12px">${review.date}</div>
                </div>
                <div class="icons">
                    <div class="frame-2610462">
                        <img id="heartIcon${review.id}" class="heart-5" src="/img/heart.svg" alt="heart" onclick="likeFunction(${review.id})"/>
                        <div id="likesCount${review.id}" class="address-5 small-text">${review.likes} 좋아요</div>
                    </div>
                    ${editAndDeleteButtons}
                </div>
                <div class="divider"></div>
            </div>
        `;
}

function editFunction(id) {
    window.location.href = '/reviews/edit/' + id;
}

function deleteFunction(id) {
    if (confirm("정말 삭제하시겠습니까?")) {
        fetch(`/api/reviews/` + id, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    alert("리뷰가 성공적으로 삭제되었습니다.");
                    location.reload();
                } else {
                    console.error('리뷰 삭제에 실패하였습니다.');
                }
            })
            .catch(error => {
                console.error(error);
            });
    }
}

function likeFunction(id) {
    const reviewId = id;
    const userId = document.getElementById('userId').value;
    const data = {
        userId: userId,
        reviewId: reviewId
    };

    fetch(`/api/reviews/${id}/likes`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                if (response.status === 404) {
                    return response.json().then(json => {
                        alert("비회원은 좋아요를 누를 수 없습니다.");
                        throw new Error('비회원은 좋아요를 누를 수 없습니다.');
                    });
                }
                if (response.status === 409) {
                    return response.json().then(json => {
                        const errorMessage = json.message;
                        alert(errorMessage);
                        throw new Error(errorMessage);
                    });
                }
                throw new Error('서버에서 오류 응답을 받았습니다.');
            }
            return response.json();
        })
        .then(data => {
            const heartIcon = document.getElementById(`heartIcon${reviewId}`);
            const likesCount = document.getElementById(`likesCount${reviewId}`);

            if (data.liked) {
                heartIcon.src = "/img/heart-liked.svg";
            } else {
                heartIcon.src = "/img/heart.svg";
            }
            likesCount.textContent = `${data.likes} 좋아요`;
        })
        .catch(error => {
            console.error(error);
        });
}

document.addEventListener("DOMContentLoaded", function () {
    // 검색한 제목으로 리뷰 로드
    fetchReviews(currentPage, currentTitle, currentAuthor, '');

    // '인기순' 리뷰 로드
    document.getElementById('popularity').addEventListener('click', () => {
        fetchReviews(currentPage, currentTitle, '', 'popularity');
    });

    // '최신순' 리뷰 로드
    document.getElementById('latest').addEventListener('click', () => {
        fetchReviews(currentPage, currentTitle, '', 'latest');
    });

    // 검색 입력 필드
    const inputField = document.querySelector('.x04-search-input input');

    inputField.addEventListener('keypress', function (event) {
        if (event.key === 'Enter') {
            const inputValue = inputField.value;
            if (inputValue.trim() !== '') {
                currentTitle = inputValue;
                fetchReviews(currentPage, currentTitle, '', '');
            }
        }
    });
});
