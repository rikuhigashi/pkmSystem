import {defineStore} from "pinia";
import {ref} from "vue";

export const useImageStore = defineStore('imageStore', () => {
  // 用于存储待上传的图片文件和其预览链接
  const pendingImages = ref<Map<File, string>>(new Map())

  // 用于存储已上传的图片文件和其预览链接
  const uploadedImages = ref<Set<string>>(new Set())

  // 当前文档使用的所有图片URL
  const currentDocImages = ref<Set<string>>(new Set());


  // 添加文件和其预览
  const addImage = (file: File, blobUrl: string) => {
    pendingImages.value.set(file, blobUrl)
  }

  // 获取所有待上传的图片文件
  const getAllPendingImages = () => {
    return Array.from(pendingImages.value.keys())
  }

  // 删除已上传的图片
  const removeImage = (file: File) => {
    // 先释放blob URL
    const blobUrl = pendingImages.value.get(file)
    if (blobUrl) {
      URL.revokeObjectURL(blobUrl)
    }
    pendingImages.value.delete(file)
  }

  // 添加已上传的图片 URL
  const addUploadedImage = (imageUrl: string) => {
    uploadedImages.value.add(imageUrl)
  }

  // 获取所有已上传的图片 URL
  const getAllUploadedImages = () => {
    return Array.from(uploadedImages.value)
  }

  // 删除已上传的图片 URL
  const removeUploadedImage = (imageUrl: string) => {
    uploadedImages.value.delete(imageUrl)
  }

  // 当前文档使用的图片
  const setCurrentDocImages = (urls: string[]) => {
    currentDocImages.value = new Set(urls);
  };

  // 不再使用的图片URL
  const getUnusedImages = (): string[] => {
    return Array.from(uploadedImages.value).filter(
      url => !currentDocImages.value.has(url)
    );
  };

  return {
    pendingImages,
    uploadedImages,
    currentDocImages,
    addImage,
    getAllPendingImages,
    removeImage,
    addUploadedImage,
    getAllUploadedImages,
    removeUploadedImage,
    setCurrentDocImages,
    getUnusedImages,
  }
})
