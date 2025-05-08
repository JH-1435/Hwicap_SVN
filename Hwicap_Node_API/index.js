import express from 'express';
import jwt from 'jsonwebtoken';
import cookieParser from 'cookie-parser';
import cors from 'cors'; // cors
import dotenv from 'dotenv';
import { getWelcomeTemplate, sendTemplateToEmail } from './email.js';

dotenv.config(); // .env 파일 로드

const app = express();
const secretKey = process.env.JWT_SECRET_KEY; // 환경 변수에서 시크릿키 가져오기

// CORS 설정 (모든 출처에서 요청을 허용)
app.use(cors({
  origin: '*', // 또는 특정 URL을 허용하려면 'http://localhost:8090' 등으로 설정
  methods: ['GET', 'POST'],
  allowedHeaders: ['Content-Type', 'Authorization'],
}));

app.use(cookieParser()); // 쿠키 파싱 미들웨어
app.use(express.json());

// 루트 경로에 대한 핸들러 추가
app.get('/', (req, res) => {
  res.send('Hello, NodeJS World!');
});

// /token endPoint - JWT 토큰 발급
app.post('/token', (req, res) => {
  // POST 본문에서 사용자 ID를 받음
  const accountId = req.body.accountId; // accountId(userId, adminId)는 POST body에서 받음

  if (!accountId) {
    return res.status(400).json({ error: 'accountId is required' });
  }

  // JWT 생성 시 알고리즘을 명시적으로 설정
  const token = jwt.sign({ accountId: accountId }, secretKey, {
    algorithm: 'HS256', // 서명 알고리즘을 HS256으로 명시적으로 설정
    expiresIn: '24h',    // 토큰 만료 시간 설정 (24시간)
    subject: accountId  // `sub` 필드를 설정
  });

  // JWT 토큰을 응답으로 반환
  res.json({ jwtToken: token });  // JWT 토큰을 클라이언트에 반환
});


// 회원가입 시 가입환영 메일 전송
app.post("/sendEmail", async function(req, res) {
  // 구조분해할당
  const { userId, name, tel, email } = req.body;
      
  // 1. 가입환영 템플릿 생성
  const myTemplate = getWelcomeTemplate({userId, name, tel, email});
  
  // 2. 이메일로 가입환영 템플릿 전송
  // sendTemplateToEmail({name, email}, myTemplate);

  try {
    // 2. 이메일 전송 (비동기 방식으로 처리)
    await sendTemplateToEmail({ name, email }, myTemplate);

    // 이메일 전송 성공
    console.log(`가입환영 이메일이 ${email}로 전송되었습니다.`);

    // 성공 시 200 상태 코드와 함께 응답
    return res.status(200).json({
      message: '가입환영 이메일이 전송되었습니다.'
    });
  } catch (error) {
    // 이메일 전송 실패 처리
    console.log('Error occurred: ' + error.message);

    // 실패 시 500 상태 코드와 함께 응답
    return res.status(500).json({
      message: '이메일 전송에 실패하였습니다.',
      error: error.message
    });
  }

});

app.listen(3000, '0.0.0.0', () => {
  console.log('Node.js server running on http://EC2-Server:3000');
});
