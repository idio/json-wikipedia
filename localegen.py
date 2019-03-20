# -*- coding: utf-8 - *-
from collections import OrderedDict
import requests
import codecs
import itertools
import pycountry
import argparse


class WikipediaLocaleGenerator:

    def __init__(self, language):
        self.language = language
        self.endpoint = "https://{lang}.wikipedia.org/w/api.php?action=query&meta=siteinfo&siprop=namespaces|namespacealiases|magicwords&format=json&formatversion=2".format(lang=language)
        self.metadata = self.query_metadata()

    def query_metadata(self):
        response = requests.get(self.endpoint, headers={"Accept-Charset ": "utf-8"})
        return response.json()

    def to_properties(self, json_data):
        lines = list()
        for key, value in json_data.items():
            if isinstance(value, list):
                lines.append("%s=%s" % (key, ",".join(value)))
            else:
                lines.append("%s=%s" % (key, value))
        return "\n".join(lines)

    def generate_locale(self, output_path):
        output = codecs.open(output_path, 'w', 'utf-8')
        metadata = OrderedDict()
        metadata['language'] = self.get_language_keyword()
        metadata['disambiguation'] = self.get_disambiguation_keywords()
        metadata['category'] = self.get_category_keywords()
        metadata['image'] = self.get_file_keywords()
        metadata["namespaces"] = self.get_ne_keywords()
        metadata["redirect"] = self.get_redirect_keywords()

        content = self.to_properties(metadata)
        output.write(content)
        output.close()

    def get_namespace_with_aliases(self, canonical_name):
        """
        Given a canonical name for a NE. It returns all possible names for the given NE
        """
        # getting the ne
        nes = self.metadata["query"]["namespaces"].values()
        namespace = list(filter(lambda x: "canonical" in x and x["canonical"] == canonical_name, nes))[0]

        # getting their possible aliases
        aliases = filter(lambda x: x["id"] == namespace['id'], self.metadata["query"]["namespacealiases"])
        aliases = [alias["alias"] for alias in aliases]
        aliases.append(namespace["name"])
        aliases.append(namespace["canonical"])
        aliases = [alias.replace(" ", "_") for alias in aliases]
        return aliases

    def get_list_keyword(self):
        # I think it is not possible to get it as it is not officially a keyword
        return ["list"]

    def get_language_keyword(self):
        """
        returns long language name 
        """
        language = pycountry.languages.get(alpha_2=self.language)
        # some languages will be returned as "Spanish; castellano"
        language = language.name.split(";")[0]
        return language

    def get_magicword(self, canonical_name):
        magicwords = self.metadata["query"]["magicwords"]
        word = list(filter(lambda x: x["name"] == canonical_name, magicwords))
        aliases = word[0]["aliases"]
        aliases.append(canonical_name)
        return aliases

    def get_redirect_keywords(self):
        """
        returns the redirect keywords from wikipedia magicwords
        """
        return self.get_magicword("redirect")

    def get_ne_keywords(self):
        """
        returns all the NE names which do not correspond to the main NE (0)
        """
        # get all ne except the top one
        all_ne = filter(lambda x: x["id"] != 0, self.metadata["query"]["namespaces"].values())
        all_ne_with_aliases = [self.get_namespace_with_aliases(ne["canonical"]) for ne in all_ne]
        # flattening the list
        return list(itertools.chain(*all_ne_with_aliases))

    def get_category_keywords(self):
        """
        returns the category keywords by looking at the namespaces
        """
        return self.get_namespace_with_aliases("Category")

    def get_file_keywords(self):
        """
        returns all aliases for file NE
        """
        return self.get_namespace_with_aliases("File")

    def get_disambiguation_keywords(self):
        """
        returns all disambiguation keywords by taking a look at the magicwords
        """
        disambiguation_template_uri = "https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q6148868&props=sitelinks&format=json"
        r = requests.get(disambiguation_template_uri)
        disambiguation_templates = r.json()

        language_disambiguation_directive = disambiguation_templates["entities"]["Q6148868"]["sitelinks"]["%swiki"%self.language]["title"].split(":")[1]

        return [language_disambiguation_directive]


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--lang", help='language i.e: fr')
    parser.add_argument("--o", help='output path where locale will be saved')
    args = parser.parse_args()

    lang = args.lang
    output_arg = args.o
    locale_generator = WikipediaLocaleGenerator(lang)
    locale_generator.generate_locale(output_arg)

    print("locale for %s generated in %s" % (locale_generator.get_language_keyword(), output_arg))

if __name__ == "__main__":
    main()
