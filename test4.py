def display_menu():
    print("===== Game Menu =====")
    print("1. Tiếp tục")
    print("2. New game")
    print("3. Cài đặt")
    print("4. Thoát")

def display_new_game_menu():
    print("===== Chọn Màn Chơi =====")
    print("1. Map 1")
    print("2. Map 2")
    print("3. Map 3")
    print("4. Quay lại")

def display_settings_menu():
    print("===== Cài Đặt =====")
    print("1. Chỉnh âm thanh")
    print("2. Chỉnh hình ảnh")
    print("3. Ngôn ngữ")
    print("4. Quay lại")

def game_menu():
    while True:
        display_menu()
        choice = input("Nhập lựa chọn của bạn: ")
        if choice == "1":
            print("Tiếp tục trò chơi...")
        elif choice == "2":
            new_game_menu()
        elif choice == "3":
            settings_menu()
        elif choice == "4":
            print("Thoát trò chơi. Tạm biệt!")
            break
        else:
            print("Lựa chọn không hợp lệ. Vui lòng chọn lại.")

def new_game_menu():
    while True:
        display_new_game_menu()

        choice = input("Chọn màn chơi của bạn: ")

        if choice == "1":
            print("Bắt đầu Map 1...")
            break
        elif choice == "2":
            print("Bắt đầu Map 2...")
            break
        elif choice == "3":
            print("Bắt đầu Map 3...")
            break
        elif choice == "4":
            print("Quay lại menu chính.")
            break
        else:
            print("Lựa chọn không hợp lệ. Vui lòng chọn lại.")

def settings_menu():
    while True:
        display_settings_menu()

        choice = input("Chọn cài đặt của bạn: ")

        if choice == "1":
            print("Chỉnh âm thanh...")
        elif choice == "2":
            print("Chỉnh hình ảnh...")
        elif choice == "3":
            print("Chọn ngôn ngữ...")
        elif choice == "4":
            print("Quay lại menu chính.")
            break
        else:
            print("Lựa chọn không hợp lệ. Vui lòng chọn lại.")

# Chạy menu chính
game_menu()
