function clickAdoption(element){
    const id = element.getAttribute('data-id');
    const url = element.getAttribute('data-url');
    if (!id) return;

    // 조회수 증가 요청 (PATCH 요청)
    fetch(`/free-adoption/${id}`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json"
        }
    }).then(response => {
        if (!response.ok) {
            console.error("Failed to update view count");
        }
    }).catch(error => console.error("Error:", error));

    // 원래 URL 새 창에서 열기
    if (url) {
        window.open(url, "_blank");
    }
}
function clickSearchButton(){
    const filters = document.querySelectorAll('.filters select');
    const queryParams = new URLSearchParams();

    filters.forEach(filter => {
        if (filter.value !== "all") { // "all"이 아닐 때만 추가
            queryParams.append(filter.name, filter.value);
        }
    });
    // URL 이동
    window.location.href = `/free-adoption?${queryParams.toString()}`;
}