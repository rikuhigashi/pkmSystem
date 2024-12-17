/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        bodyBackground:"#f4f4f4"
      },
      spacing:{
        sidebarTranlate :"400rem"
      }
    },
  },
  plugins: [],
}

