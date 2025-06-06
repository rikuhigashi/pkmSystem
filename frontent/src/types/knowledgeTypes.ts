import type { JSONContent } from '@tiptap/core'

export interface KnowledgeItem {
  id: string | number;
  title: string;
  content: JSONContent;
  author: string;
  createdAt: string;
  price: number;
  isEncrypted: boolean;
  purchased: boolean;
  tags?: string[];
}

export interface ShareFormData {
  title: string;
  isEncrypted: boolean;
  price: number;
  tags: string[];
}
