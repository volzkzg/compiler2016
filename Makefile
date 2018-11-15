all: clean
	@mkdir -p ./bin
	@cd ./src && javac -cp \
		./antlr-4.5.2-complete.jar \
		./compiler/ast/*.java \
		./compiler/ast/*/*.java \
		./compiler/ast/*/*/*.java \
		./compiler/ast/*/*/*/*.java \
		./compiler/build/*.java \
		./compiler/parser/*.java \
		./compiler/ir/*.java \
		-d ../bin
	@cp ./src/antlr-4.5.2-complete.jar ./bin
	@cd ./bin && jar xf ./antlr-4.5.2-complete.jar \
			  && rm -rf ./META-INF \
			  && jar cef compiler/build/Build Compiler.jar ./ \
			  && rm -rf ./antlr-4.5.2-complete.jar ./Compiler ./org

clean:
	rm -rf ./bin
