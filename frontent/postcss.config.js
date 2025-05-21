// export default {
//   plugins: {
//     tailwindcss: {},
//     autoprefixer: {},
//   },
// }
export default {
  plugins: {
    'postcss-import': {},
    '@tailwindcss/postcss': {
      config: './tailwind.config.js'
    },
    'postcss-nested': {},
    autoprefixer: {}
  }
}
