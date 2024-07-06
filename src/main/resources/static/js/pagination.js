function setupPagination(totalElements, currentPage) {
    const paginationContainer = document.getElementById('pagination');

    paginationContainer.innerHTML = '';
    const totalPages = Math.ceil(totalElements / pageSize);
    const maxVisibleButtons = 5;

    if (totalPages <= 1) {
        return;
    }

    const createPageButton = (pageNum) => {
        const pageButton = document.createElement('button');
        pageButton.textContent = pageNum + 1;
        pageButton.addEventListener('click', () => {
            currentPage = pageNum;
            fetchReviews(currentPage, currentTitle, '', '');
        });
        if (pageNum === currentPage) {
            pageButton.disabled = true;
        }
        return pageButton;
    };

    paginationContainer.appendChild(createPageButton(0));

    if (totalPages <= maxVisibleButtons) {
        for (let i = 1; i < totalPages; i++) {
            paginationContainer.appendChild(createPageButton(i));
        }
    } else {
        if (currentPage < maxVisibleButtons - 1) {
            for (let i = 1; i < maxVisibleButtons - 1; i++) {
                paginationContainer.appendChild(createPageButton(i));
            }
            paginationContainer.appendChild(document.createTextNode('...'));
            paginationContainer.appendChild(createPageButton(totalPages - 1));
        } else {
            paginationContainer.appendChild(document.createTextNode('...'));
            const startPage = Math.max(1, currentPage - 2);
            const endPage = Math.min(totalPages - 1, currentPage + 2);

            for (let i = startPage; i <= endPage; i++) {
                paginationContainer.appendChild(createPageButton(i));
            }

            if (currentPage < totalPages - 3) {
                paginationContainer.appendChild(document.createTextNode('...'));
                paginationContainer.appendChild(createPageButton(totalPages - 1));
            }
        }
    }
}