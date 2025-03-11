<!--注册验证码邮件模板-->

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>验证码通知</title>
</head>
<body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
<!-- 邮件容器 -->
<table style="border-collapse: collapse; width: 100%; max-width: 600px; margin: 0 auto;">
    <!-- 头部 -->
    <tr>
        <td style="background-color: #333333; color: #fff; text-align: center; padding: 20px;">
            <h1 style="margin: 0; font-weight: bold;">image-hosting</h1>
        </td>
    </tr>
    <!-- 内容 -->
    <tr>
        <td style="padding: 0; text-align: left;">
            <h4 style="margin-bottom: 5px;">您的验证码是:</h4>
        </td>
    </tr>
    <tr style="text-align: center; padding: 20px;">
        <td style="margin: 20px auto 0; padding: 20px; background-color: #f9f9f9; display: inline-block; text-align: center;">
            <h2 style="font-size: 30px; font-weight: bolder; margin: 0; background: linear-gradient(to right, #000000, #000000); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">${code}</h2>
        </td>
    </tr>
    <tr>
        <td style="text-align: left;">
        <p style="margin: 20px 0 10px;">验证码5分钟内有效，请勿泄露给他人。</p>
        <p style="margin-top: 60px; font-size: 14px">诚挚祝福,</p>
        <p style="font-size: 14px;margin-bottom: 10px">image-hosting团队</p>
        </td>
    </tr>
    <!-- 底部 -->
    <tr>
        <td style="background-color: #333333; color: #8a8a8a; text-align: center; padding: 10px; font-size: 8px;">
            <p style="margin: 0 0 5px;">&copy; 2025 image-hosting. All rights reserved.</p>
            <p style="margin: 5px 0;"><a href="https://example.com" style="color: #ffffff; text-decoration: none;">访问官网</a></p>
        </td>
    </tr>
</table>
</body>
</html>