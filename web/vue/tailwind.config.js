/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      height: {
        '500': '500px',
      },
      colors: {
        // primary: '#1D4ED8',
        // secondary: '#FFFFFF',
      },
      spacing: {
        18: '4.5rem', // 新增 4.5rem 的间距
      },
      fontSize: {
        '4xl': '2.5rem',
      },
      borderRadius: {
        'xl': '1.5rem',
      },
      screens: {
        '2xl': '1536px', // 添加新的响应式断点
      },
    },
  },
  plugins: [],
}

