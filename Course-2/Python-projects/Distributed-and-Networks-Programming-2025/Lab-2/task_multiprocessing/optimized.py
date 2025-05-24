import asyncio
import time


nums = []
step = 1000


async def check_primes(left, right, idx):
    global nums, step
    for num in range(max(left, 2), right):
        prime = True
        for div in range(2, int(num ** 0.5) + 1):
            if num % div == 0:
                prime = False
                break
        if prime:
            nums[idx].append(num)


async def main():
    global nums, step
    num = 1000000

    nums = [[] for _ in range(num // step)]
    idx = 0

    for i in range(0, num, step):
        await check_primes(i, i + step, idx)
        idx += 1

    file = open("primes_optimized.txt", "w")
    for i in range(len(nums)):
        for j in range(len(nums[i])):
            if (i == len(nums) - 1) and (j == len(nums[i]) - 1):
                file.write(str(nums[i][j]))
            else:
                file.write(str(nums[i][j]) + "\n")
    file.close()


if __name__ == "__main__":
    asyncio.run(main())
