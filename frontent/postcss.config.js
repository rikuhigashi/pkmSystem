// export default {
//   plugins: {
//     tailwindcss: {},
//     autoprefixer: {},
//   },
// }
export default {
  plugins: {
    'postcss-import': {},
    'tailwindcss/nesting': {},
    'tailwindcss': { config: './tailwind.config.js' },
    'postcss-nested': {},
    'autoprefixer': {}
  }
}
