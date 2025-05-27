import apiClient from "@/API/axios";

// 创建标签
export const createTag = (name:string) => {
    return apiClient.post("/tags/createTag",name);
};

// 更新标签名称
export const updateTagName = (tagId: number, newName: string) => {
    return apiClient.patch(`/tags/${tagId}/name`, null, {
        params: { newName }
    });
};

// 删除标签
export const deleteTag = (tagId: number) => {
    return apiClient.delete(`/tags/${tagId}`);
};

// 获取用户的所有标签
export const getUserTags = () => {
    return apiClient.get("/tags/getUserTags");
};

// 将数据移动到指定标签
export const moveToTag = (dataId: number, tagId: number) => {
    return apiClient.put(`/sideData/${dataId}/tag/${tagId}`);
};

// 从标签中移除数据
export const removeFromTag = (dataId: number) => {
    return apiClient.delete(`/sideData/${dataId}/tag`);
};

