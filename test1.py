class Person:
    def __init__(self, ho_ten, tuoi):
        self.ho_ten = ho_ten
        self.tuoi = tuoi

def them_nguoi(danh_sach_nguoi):
    ho_ten = input("Nhập họ và tên: ")
    tuoi = int(input("Nhập tuổi: "))
    nguoi_moi = Person(ho_ten, tuoi)
    danh_sach_nguoi.append(nguoi_moi)
    print(f"{ho_ten} đã được thêm vào danh sách.")

def xoa_nguoi(danh_sach_nguoi):
    ho_ten = input("Nhập họ và tên của người cần xóa: ")
    for nguoi in danh_sach_nguoi:
        if nguoi.ho_ten == ho_ten:
            danh_sach_nguoi.remove(nguoi)
            print(f"{ho_ten} đã được xóa khỏi danh sách.")
            return
    print(f"Không tìm thấy người có tên {ho_ten} trong danh sách.")

def hien_thi_danh_sach(danh_sach_nguoi):
    print("\nDanh sách người:")
    for index, nguoi in enumerate(danh_sach_nguoi, 1):
        print(f"{index}. {nguoi.ho_ten}, {nguoi.tuoi} tuổi")

def main():
    danh_sach_nguoi = []

    while True:
        print("\nChọn chức năng:")
        print("1. Thêm người")
        print("2. Xóa người")
        print("3. Thoát")

        chon = input("Nhập lựa chọn của bạn: ")

        if chon == "1":
            them_nguoi(danh_sach_nguoi)
        elif chon == "2":
            xoa_nguoi(danh_sach_nguoi)
        elif chon == "3":
            print("Chương trình kết thúc.")
            break
        else:
            print("Lựa chọn không hợp lệ. Vui lòng chọn lại.")

        hien_thi_danh_sach(danh_sach_nguoi)

if __name__ == "__main__":
    main()


"""
Dùng hàm tạo ra một Menu của trò chơi gồm các chức năng: 
   1 tiếp tục
   2 new gamme
   3 cài đặt
   4 thoát
Trong phần new game có phần chọn các màn chơi
1. Map 1
2. Map 2
3. Map 3
4. Quay lại
Trong phần cài đặt 
1. Chỉnh âm thanh 
2. Chỉnh hình ảnh
3. Ngôn ngữ
4. Quay lại 
"""