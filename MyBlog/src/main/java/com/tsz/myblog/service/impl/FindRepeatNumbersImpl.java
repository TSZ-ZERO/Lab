package com.tsz.myblog.service.impl;

import com.tsz.myblog.service.FindRepeatNumbers;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class FindRepeatNumbersImpl implements FindRepeatNumbers {
    @Override
    public List<Integer> find(int[] nums) {
        List<Integer> result = new ArrayList<>();
        // 先对数组进行排序，使相同的元素相邻
        Arrays.sort(nums);
        // 使用双指针遍历数组
        int left = 0;
        while (left < nums.length - 1) {
            // 如果当前元素与下一个元素相同，则为重复元素
            if (nums[left] == nums[left + 1]) {
                // 将重复元素添加到结果列表
                result.add(nums[left]);
                // 跳过所有相同的重复元素
                while (left < nums.length - 1 && nums[left] == nums[left + 1]) {
                    left++;
                }
            }
            // 移动指针到下一个元素
            left++;
        }
        return result;
    }
}
