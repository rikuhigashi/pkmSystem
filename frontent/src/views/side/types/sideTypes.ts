import type {Component} from "vue";

export interface sideListItem {
  id:  number
  name: string
  href: string
  icon: string
  current: boolean
  iconComponent:Component
  tiptapJson: Record<string, unknown>
  // initial?: string
}
