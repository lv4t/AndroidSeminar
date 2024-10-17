# Xây dựng các kẻ thù

import pygame, random, time
from pygame.locals import *

pygame.init()

# Thiết lập thông số cho game
FPS = 120 # 24
fpsClock = pygame.time.Clock()

width = 800
height = 600
window = pygame.display.set_mode((width, height))
pygame.display.set_caption('Cửa sổ game')

bg = pygame.image.load('bg.jpg')
bg = pygame.transform.scale(bg, (width, height))

font = pygame.font.SysFont('consolas', 30)
textSurface = font.render(" Collect apples! ", True, (255, 255, 255), (243, 75, 17)) 

timer = 15

# Nhạc nền
bg_music = pygame.mixer.Sound('bg_music.mp3')
bg_music.play(loops=-1)
bg_music.set_volume(0.5) # 0-1

# Các hiệu ứng âm thanh
pop_fx = pygame.mixer.Sound('pop.wav')
pop_fx.set_volume(1)
bark_fx = pygame.mixer.Sound('barking.mp3')
bark_fx.set_volume(1)

class Character:
    def __init__(self, x, y, link, size):
        self.x = x
        self.y = y
        self.size = size
        self.image = pygame.image.load(link)

    def draw(self, surface):
        self.image = pygame.transform.scale(self.image, self.size)
        surface.blit(self.image, (self.x, self.y))

    def control(self, moveLeft, moveRight, moveUp, moveDown):
        if moveLeft == True:
            self.x = self.x - 2
        if moveRight == True:
            self.x = self.x + 2
        if moveUp == True:
            self.y = self.y - 2
        if moveDown == True:
            self.y = self.y + 2

        if self.x + 100 > width:
            self.x = width - 100
        if self.x < 0:
            self.x = 0
        if self.y + 100 > height:
            self.y = height - 100
        if self.y < 0:
            self.y = 0

    def run(self, speed, direct):
        if direct == 1: # Horizontal
            if self.x < 0:
                self.x = width
            else:
                self.x = self.x - speed


# Các chương trình con
def collision(surface1, pos1, surface2, pos2):
    mask1 = pygame.mask.from_surface(surface1)
    mask2 = pygame.mask.from_surface(surface2)
    x = pos2[0] - pos1[0]
    y = pos2[1] - pos1[1]
    if mask1.overlap(mask2, (x, y)) != None:
        return True
    return False

def text(font_size, txt, color):
    font = pygame.font.SysFont('consolas', font_size)
    textSurface = font.render(txt, True, color) 
    return textSurface

# Khởi tạo nhân vật
char = Character(330, 400, 'char.png', (130, 130))
apple = Character(150, 400, 'char2.png', (50, 50))
dog = Character(width - 100, 100, 'dog.png', (70, 70))
dog2 = Character(width, 400, 'dog.png', (70, 70))

# Biến chương trình
moveLeft = False
moveRight = False
moveUp = False
moveDown = False

# Biến hệ thống
running = True
sound_check = True
score = 0
start_time = pygame.time.get_ticks()

# Chương trình chính
while running == True:

    # Các câu lệnh xử lí sự kiện
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

        if event.type == KEYDOWN:
            if event.key == K_LEFT:
                moveLeft = True
            if event.key == K_RIGHT:
                moveRight = True
            if event.key == K_UP:
                moveUp = True
            if event.key == K_DOWN:
                moveDown = True
        
        if event.type == KEYUP:
            if event.key == K_LEFT:
                moveLeft = False
            if event.key == K_RIGHT:
                moveRight = False
            if event.key == K_UP:
                moveUp = False
            if event.key == K_DOWN:
                moveDown = False
    
    # Cài đặt ảnh nền
    window.blit(bg, (0, 0))
    window.blit(textSurface, (280, 50))

    # Các câu lệnh cho nhân vât char
    char.draw(window)
    char.control(moveLeft, moveRight, moveUp, moveDown)

    # Các câu lệnh cho nhân vât dog
    dog.draw(window)
    dog.run(3, 1)
    dog2.draw(window)
    dog2.run(3, 1)

    # Các câu lệnh tương tác giữa các nhân vật
    if collision(char.image, (char.x, char.y), apple.image, (apple.x, apple.y)) == False:
        apple.draw(window)
        sound_check = True
    else:
        if sound_check == True:
            pop_fx.play()
            score = score + 1
            apple.x = random.randrange(100, width-100)
            apple.y = random.randrange(100, height-100)
            #print(score)
            sound_check = False

    if collision(char.image, (char.x, char.y), dog.image, (dog.x, dog.y)) == True:
        bark_fx.play()
        time.sleep(0.5)
        running = False
    if collision(char.image, (char.x, char.y), dog2.image, (dog2.x, dog2.y)) == True:
        bark_fx.play()
        time.sleep(0.5)
        running = False

    score_txt = text(30, "Score: "+str(score), (255, 0, 0))
    window.blit(score_txt, (350, height-35))

    # Các câu lệnh hệ thống
    current_time = pygame.time.get_ticks()
    elapsed_time = (current_time - start_time) // 1000
    print(elapsed_time)
    if elapsed_time >= timer:
        print("Game over!")
        running = False

    pygame.display.update()
    fpsClock.tick(FPS)

pygame.quit()