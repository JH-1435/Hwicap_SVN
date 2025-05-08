// 금일 날짜(년월일) 생성
export function getTodayDate () {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
    const day = String(today.getDate()).padStart(2, '0');

    const createAt = `${year}-${month}-${day}`;
    return createAt;
}