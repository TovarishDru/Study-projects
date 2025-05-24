def main():
    primes = []
    for num in range(2, 1000001):
        prime = True
        for div in range(2, int(num ** 0.5) + 1):
            if num % div == 0:
                prime = False
                break
        if prime:
            primes.append(num)
    file = open("primes_nonoptimized.txt", "w")
    for i in range(len(primes)):
        if i != len(primes) - 1:
            file.write(str(primes[i]) + "\n")
        else:
            file.write(str(primes[i]))
    file.close()


if __name__ == "__main__":
    main()
