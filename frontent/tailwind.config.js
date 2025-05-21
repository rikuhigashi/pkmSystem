/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
    './src/assets/icons/*.vue'
  ],
  theme: {
    extend: {
      colors: {
        bodyBackground: '#f4f4f4',
      },
      fontFamily: {
        sans: ['Poppins', 'sans-serif'],
      },
      spacing: {
        sidebarTranlate: '400rem',
      },
    },
  },
  // eslint-disable-next-line @typescript-eslint/no-require-imports
  plugins: [require('daisyui'),require('@tailwindcss/forms')]

}
