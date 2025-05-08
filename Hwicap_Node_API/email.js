import nodemailer from 'nodemailer';
import { getTodayDate } from './utils.js';
import 'dotenv/config' // 환경변수(.env)파일 읽는 라이브러리


// 가입환영 템플릿 생성
export function getWelcomeTemplate ({userId, name, tel, email}) {
    const createAt = getTodayDate();
    // 최신 HTML, CSS 사용하지 않기. 버전에 영향을 끼침
    const myTemplate = `
        <html>
            <body>
                <div style="display: flex; flex-direction: column; align-items: center;">
                    <div style="width: 500px;">
                        <h1>${userId}님 Hwicap 가입을 환영합니다. !!!</h1>
                        <hr />
                        <h2>사용자 정보</h2>
                        <div style="color: green;">이름: ${name}</div>
                        <div>전화번호: ${tel}</div>
                        <div>메일주소: ${email}</div>
                        <div>가입일: ${createAt}</div>
                    </div>
                </div>
            </body>
        </html>
    `;

    return myTemplate;
}

// 이메일로 가입환영 템플릿 전송
export async function sendTemplateToEmail({name, email}, myTemplate) {

    // .env 에서 가져온 구글 이메일, 구글 앱 비밀번호 KEY
    const GOOGLE_EMAIL = process.env.GOOGLE_EMAIL;
    const APP_KEY = process.env.APP_KEY;

    // nodeMailer : 이메일 전송
    const transporter=  nodemailer.createTransport({
        service: "gmail",
        auth: {
            user: GOOGLE_EMAIL, // 구글 이메일
            pass: APP_KEY       // 구글-앱 비밀번호 KEY: myAppPw
        }
    });

    const response = await transporter.sendMail({
        from: GOOGLE_EMAIL,
        to: email,
        subject: "[테스트] Hwicap 가입을 축하합니다!!!",
        html: myTemplate
    });

}