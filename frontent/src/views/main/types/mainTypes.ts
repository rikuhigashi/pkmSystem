export type toolbarItem = ButtonItem | DividerItem |DropdownItem

export interface ButtonItem {
  type: 'button'
  icon: string
  title: string
  action: (event?: MouseEvent) => void
  isActive?: () => boolean
  disabled?: () => boolean
  meta?: {
    type?: string
  }
}

export interface DividerItem {
  type: 'divider'
}

export interface DropdownItem {
  type: 'dropdown'
  icon: string
  title: string
  options: Array<{
    label: string | {type: 'icon', name: string }
    value: string
  }>
  action: (value: string) => void
  isActive?: (value?: string) => boolean
  meta?: {
    type?: string
  }
}


export interface mainPosition {
  top: number
  left: number
}
