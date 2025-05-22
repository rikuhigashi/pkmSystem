/** @type {import('tailwindcss').Config} */
import tailwindForms from '@tailwindcss/forms'
import typography from '@tailwindcss/typography'

export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
    './src/assets/icons/*.vue',
    './node_modules/@tailwindcss/typography/**/*.js',
  ],
  theme: {
    extend: {
      colors: {
        bodyBackground: '#f4f4f4',
        indigo: {
          600: '#4f46e5',
          700: '#4338ca',
        },
      },
      fontFamily: {
        sans: ['Poppins', 'sans-serif'],
      },
      spacing: {
        sidebarTranslate: '400rem',
      },
    },
  },
  plugins: [tailwindForms, typography],
  daisyui: {
    themes: [
      {
        light: {
          "primary": "229 100% 55%",
          "secondary": "251 89% 67%",
          "accent": "328 85% 57%",
          "neutral": "210 20% 98%",
          "base-100": "0 0% 100%",
          "base-content": "215 28% 17%",

          // 圆角配置
          "--rounded-box": "0.5rem",
          "--rounded-btn": "0.5rem",
          "--rounded-badge": "1.9rem"
        },
      },
    ],
    base: true,
    styled: true,
    utils: true,
    prefix: 'dui-',
    logs: false,
    themeRoot: ':root',
    components: {
      input: {
        valid: 'border-success',
        invalid: 'border-error',
      },
      validator: {
        invalid: 'input-error',
        valid: 'input-success',
      },
      'validator-hint': {
        base: 'text-xs mt-1',
        valid: 'text-success',
        invalid: 'text-error',
      },
    },
  },
  safelist: [
    // 基础类
    'ring-1',
    'ring-gray-300',
    'focus:ring-2',
    'focus:ring-indigo-600',

    // 验证器相关
    'validator',
    'validator-invalid',
    'validator-hint',
    'input-error',
    'input-success',
    'text-error',
    'text-success',

    // 样式相关
    'shadow-sm',
    'rounded-md',
    // 表格相关
    'px-4',
    'py-3',
    'rounded-md',
    // 分页状态
    'btn-active',
    // 加载动画
    'loading-spinner',
    'loading-dots',
    'rounded-box',
    'z-[99]',

    // 色彩相关
    { pattern: /bg-(indigo)-(600|700)/ },
    { pattern: /text-(gray)-(900|700)/ },
    { pattern: /text-(success|error)/ },
    { pattern: /(border|text)-(error|success)/, variants: ['focus', 'hover'] },

    // 动画和过渡相关
    { pattern: /(opacity|duration|transition)-/, variants: ['group-hover', 'hover', 'focus'] },

    // 间距和布局相关
    { pattern: /(mt|text)-(1|xs)/ },
    { pattern: /(flex|items|justify|space)-(x|y|between)/ },

    // dui 样式相关
    { pattern: /(bg|text|border)-dui-(indigo|gray)-/ },
    { pattern: /focus:ring-dui-/ },

    // 通用表单元素
    { pattern: /(btn|input|form-control|label|checkbox|link)/ },
    { pattern: /(alert|validator|validator-hint)/ },

    // 色彩分类样式
    { pattern: /(bg|text|border)-(primary|secondary|accent|success|error)/ },

    // 间距和布局相关（更多）
    { pattern: /(mt|space|gap|py|px|pl|pr|pt|pb|ml|mr)-/ },

    // 输入框状态
    { pattern: /(input-error|input-success)/ },

    // 按钮、输入框等控件样式
    { pattern: /(btn|input|form-control|label|checkbox|link|alert)/ },

    // 验证器和输入框状态
    { pattern: /(validator|validator-hint|input-error|input-success)/ },

    // 色彩样式
    { pattern: /(bg|text|border)-(primary|secondary|accent|success|error|indigo|gray)/ },
    { pattern: /(text|bg)-(indigo|gray)-(600|700|900)/ },

    // 间距和布局（更多）
    { pattern: /(mt|space|gap|py|px|pl|pr|pt|pb|ml|mr)-/ },

    // 布局相关
    { pattern: /(flex|items|justify|between|center)/ },

    // 过渡动画
    { pattern: /(transition|duration|opacity)-/ },

    // 伪类样式
    { pattern: /(hover|focus):/ },
    { pattern: /(modal|modal-box|modal-action)/ },
    { pattern: /(join|join-item)/ },
    { pattern: /btn-active/ },
    { pattern: /(badge|text-warning|text-success|text-error)/ },
    { pattern: /(bg|text)-(orange|green|red)-(100|800)/ },
    { pattern: /(bg|hover:bg)-(purple)-(500|600)/ },
    { pattern: /(size|w|h)-(4|6|8)/ },
    { pattern: /z-\[99\]/ },
    { pattern: /(max-w-md|max-w-xs)/ },
    { pattern: /(line-clamp-2)/ },
    { pattern: /(bg-blue-50\/50)/ },
    { pattern: /(min-h-\[120px\])/ },
    { pattern: /(max-h-\[70vh\])/ },
    { pattern: /animate-pop-in/ },
    { pattern: /ring-gray-100/ },
    { pattern: /(min-w-\[160px\])/ },
    { pattern: /(space-y-4|space-y-6)/ },
    { pattern: /(rounded-xl|rounded-2xl)/ },
    { pattern: /bg-gradient-to-tr/ },
    { pattern: /from-(indigo|purple)-50\/50/ },
    { pattern: /border-t/ },
    { pattern: /(grid|sm:grid-cols-2)/ },
    { pattern: /(max-w-5xl)/ },
    { pattern: /input-lg/ },
    { pattern: /btn-lg/ },
    { pattern: /(focus:ring-2|focus:ring-indigo-500)/ },
    { pattern: /(shadow-md|hover:shadow-lg)/ },
    { pattern: /(transition-all|duration-200)/ },
    { pattern: /(text-gray-700|cursor-not-allowed)/ },
    // 状态颜色
    { pattern: /bg-(orange|green|red)-100/ },
    { pattern: /text-(orange|green|red)-800/ },
    { pattern: /-translate-x-/ },
    { pattern: /ring-(blue|gray)-/ },
    { pattern: /text-base-content/ },
    { pattern: /bg-base-100/ },
    { pattern: /-translate-x-/ },
    { pattern: /ring-(primary|gray)/ },
    { pattern: /bg-(base-100|gray-900)/ },
    { pattern: /text-(base-content|gray-400)/ },
    { pattern: /bg-base-*/ },
    { pattern: /text-base-*/ },
    { pattern: /p-[0-9]/ },
    { pattern: /px-[0-9]/ },
    { pattern: /py-[0-9]/ },
  ],
}
