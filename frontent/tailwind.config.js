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
          primary: '229 100% 55%',
          secondary: '251 89% 67%',
          accent: '328 85% 57%',
          neutral: '210 20% 98%',
          'base-100': '0 0% 100%',
          'base-content': '215 28% 17%',

          // 圆角配置
          '--rounded-box': '0.5rem',
          '--rounded-btn': '0.5rem',
          '--rounded-badge': '1.9rem',
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
    // 基础样式
    'ring-1',
    'ring-gray-300',
    'shadow-sm',
    'rounded-md',
    'rounded-box',
    'z-[99]',
    'pr-4',

    // 表单验证 & 状态提示
    'validator',
    'validator-invalid',
    'validator-hint',
    'input-error',
    'input-success',
    'text-error',
    'text-success',

    // 按钮与状态
    'btn-active',
    'btn-lg',
    'input-lg',
    'form-control',
    'label',
    'checkbox',
    'link',
    'alert',

    // 表格 & 分页
    'px-4',
    'py-3',

    // 动画 & 过渡
    'loading-spinner',
    'loading-dots',
    'animate-pop-in',
    'transition',
    'transition-all',
    'duration-200',
    'hover:shadow-lg',
    'shadow-md',
    'group-hover:opacity-100',
    'opacity-0',

    // 色彩（基础、状态、组件主题）
    { pattern: /bg-(indigo|gray|orange|green|red)-(100|600|700|800)/ },
    { pattern: /text-(gray|success|error|orange|green|red)-(400|700|800|900)/ },
    { pattern: /border-(gray|success|error|primary|secondary|accent)/ },
    { pattern: /(bg|text|border)-(primary|secondary|accent|success|error)/ },
    { pattern: /(bg|text|border)-dui-(indigo|gray)-/ },
    { pattern: /focus:ring-(indigo-500|dui-.+|2)/ },

    // 间距与布局（统一分类）
    { pattern: /(mt|space|gap|py|px|pl|pr|pt|pb|ml|mr)-/ },
    { pattern: /(mt|text)-(1|xs)/ },

    // Flex & Grid 布局
    { pattern: /(flex|items|justify|space)-(x|y|between|center)/ },
    { pattern: /(grid|sm:grid-cols-2)/ },

    // Tailwind 尺寸类
    { pattern: /(size|w|h)-(4|6|8)/ },
    { pattern: /w-4|h-4/ },
    { pattern: /(min-w-\[160px\]|min-h-\[120px\]|max-h-\[70vh\])/ },
    { pattern: /(max-w-md|max-w-xs|max-w-5xl)/ },

    // 文本控制
    { pattern: /line-clamp-2/ },
    { pattern: /text-base-content/ },
    { pattern: /text-base-.*/ },
    { pattern: /text-gray-700/ },
    { pattern: /text-(base-content|gray-400|error)/ },

    // 基础背景
    { pattern: /bg-base-100/ },
    { pattern: /bg-(base-100|gray-900)/ },
    { pattern: /bg-base-.*/ },

    // 伪类和状态变化
    { pattern: /(hover|focus):/ },
    { pattern: /(focus:ring-2|focus:ring-indigo-500)/ },
    { pattern: /(transition|duration|opacity)-/, variants: ['group-hover', 'hover', 'focus'] },
    { pattern: /(border|text)-(error|success)/, variants: ['focus', 'hover'] },

    // DaisyUI 元素
    { pattern: /(modal|modal-box|modal-action)/ },
    { pattern: /(join|join-item)/ },
    { pattern: /(badge|text-warning|text-success|text-error)/ },

    //  特效样式
    { pattern: /bg-gradient-to-tr/ },
    { pattern: /from-(indigo|purple)-50\/50/ },
    { pattern: /bg-blue-50\/50/ },

    //  位置和层级
    { pattern: /z-\[99\]/ },
    { pattern: /-translate-x-/ },
    { pattern: /ring-(blue|gray|primary)/ },
    { pattern: /cursor-not-allowed/ },

    //  Padding（数字）
    { pattern: /p-\d+/ },
    { pattern: /px-\d+/ },
    { pattern: /py-\d+/ },
    { pattern: /space-y-(4|6)/ },

    //  圆角
    { pattern: /(rounded-xl|rounded-2xl)/ },
  ],
}
